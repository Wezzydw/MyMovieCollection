/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.bll;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import mymoviecollection.be.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
import mymoviecollection.be.Movie;
import mymoviecollection.dal.CategoryDAO;
import mymoviecollection.dal.DALException;
import mymoviecollection.dal.MovieDAO;

/**
 *
 * @author mpoul
 */
public class Manager
{

    private static final int WAIT_TIME = 30000;
    private static final int ONCE_A_SECOND = 2000;
    private MovieDAO mdao;
    private CategoryDAO cdao;
    private ObservableList<Movie> movies;
    private ObservableList<Category> categories;
    private Movie movie;
    private Player vlc;
    private Search search;
    private List<Movie> allMovies;
    private List<Category> genres;
    private List<Boolean> checkCategories;
    private long movieLoop;
    private int initMovieLoopSize;
    private String globalQuery;
    private long updateOnceASecond;
    private MenuButton mmm;
    
    public Manager() throws IOException, SQLException
    {
        mdao = new MovieDAO();
        cdao = new CategoryDAO();
        search = new Search();
        allMovies = new ArrayList();
        genres = new ArrayList();
        initMovieLoopSize = 0;
        vlc = new Player();
        movies = FXCollections.observableArrayList();
        categories = FXCollections.observableArrayList();
        categories.addAll(cdao.getAllCategories());
        globalQuery = "";
        updateOnceASecond = 0;
    }

    /**
     * Kalder deleteMovie metoden i mdao som fjerner film fra databasen.
     *
     * @throws IOException
     */
    public void deleteMovie() throws IOException, SQLException
    {
        mdao.deleteMovies(movies);
    }

    /**
     * Sørger for håndtering af listen og categorierne
     * Disse opdateres efter forskellige parametrer og fungerer som et loop
     * der kalder sig selv for at opdatere
     *
     *
     * @param filepath
     */
    public void scanFolder(String filepath)
    {
        mdao.clearMovieList();
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    mdao.scanFolder(filepath);
                } catch (DALException ex)
                {
                    //Desværre ikke klar over hvordan det her skal
                    //håndteres i en runnable
                    System.out.println("Error in scanning folder " + ex);
                    
                } catch (SQLException ex)
                {
                    System.out.println("Error in scanning folder " + ex);
                }
            }
        });
        t.start();
        movieLoop = System.currentTimeMillis();
        repeatCheckMovies();
    }

    /**
     * Ligger sig selv i et loop, og opdaterer listen løbende.
     */
    private void repeatCheckMovies()
    {
        long currentTime = System.currentTimeMillis();
        List<Movie> tmpMovieList = new ArrayList();
        List<Movie> movieDao = mdao.getMovie();

        if (movieDao.size() > 0)
        {
            tmpMovieList.add(movieDao.get(movieDao.size() - 1));
        }

        if (updateOnceASecond == 0)
        {
            updateOnceASecond = currentTime;
        }

        if (initMovieLoopSize != movies.size() || initMovieLoopSize == 0)
        {
            initMovieLoopSize = movies.size();
            movieLoop = currentTime;
        }

        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                if (currentTime < movieLoop + WAIT_TIME)
                {
                    if ((updateOnceASecond + ONCE_A_SECOND) < currentTime)
                    {
                        List<Movie> listToAdd = new ArrayList();
                        for (Movie m : tmpMovieList)
                        {
                            if (!allMovies.contains(m))
                            {
                                listToAdd.add(m);
                            }
                        }
                        if (listToAdd.size() > 0)
                        {
                            allMovies.addAll(listToAdd);
                            searchMovie(globalQuery);
                            
                            ttt();
                        }

                        try
                        {
                            mdao.SendDataToDB(listToAdd);
                        } catch (IOException ex)
                        {
                            //Desværre ikke klar over hvordan det her skal
                            //håndteres i en runnable
                            System.out.println("Error in sending data to DB " + ex);
                        } catch (SQLException ex)
                        {
                            System.out.println(ex.getMessage() + ex);
                        }
                        updateOnceASecond = currentTime;
                    }
                    repeatCheckMovies();
                }
            }
        });
    }
    
    /**
     * Laver en kopi af menuen fra MODEL
     * @param mmm 
     */
    public void setMenu(MenuButton mmm)
    {
        this.mmm = mmm;
    }
    /**
     * refesher CheckMenu
     */
    public void ttt() {
        List<Category> categoriesAll = new ArrayList();
        categoriesAll.addAll(categories);
        categoriesAll.addAll(mdao.getCategory());
        mmm.getItems().clear();
        for (Category c : categoriesAll) {
            mmm.getItems().add(new CheckMenuItem(c.getTitle()));
        }
    }
    
    /**
     * redigerer en film
     * @param selectedItem
     * @throws SQLException 
     */

    public void editMovie(Movie selectedItem) throws SQLException
    {
        mdao.updateMovie(selectedItem);
    }

    /**
     * Den giver lov til at redigere kategorier og opdaterer det ned og giver
     * updateCategory i cdao adgang hertil.
     *
     * @param category
     * @param newTitle
     * @throws SQLException
     */
    public void editCategory(Category category, String newTitle) throws SQLException
    {
        categories.remove(category);
        categories.add(new Category(newTitle));
        cdao.updateCategory(category.getTitle(), newTitle);
    }

    /**
     * Åbner den highligtede film i VLC-player.
     *
     * @param selectedItem
     */
    public void playMovie(Movie selectedItem) throws IOException, SQLException
    {
        //vlc.callVlc(selectedItem.getFilePath());
        vlc.openDefaultProgram(selectedItem.getFilePath());
        mdao.SendLastView(selectedItem);

        for (Movie m : allMovies)
        {
            if (m.getTitle() == selectedItem.getTitle())
            {
                m.setLastView(selectedItem.getLastView());
            }
        }
    }

    /**
     * Sender rating ned til databasen ned til databasen, som brugeren giver
     * filmen, ved hjælp af sliderbaren.
     *
     * @param selectedItem
     * @param rating
     */
    public void sliderRateMovie(Movie selectedItem, double rating) throws IOException, SQLException
    {
        selectedItem.setRating(rating);
        mdao.SendRatingToDB(selectedItem);
    }

    /**
     * Fjerner den highlightede film, fra listviewet over tilføjede film, og
     * sender videre ned til deleteMovies i mdao.
     *
     * @param selectedItem
     * @throws IOException
     */
    public void reMovie(List<Movie> selectedItem) throws IOException, SQLException
    {
        allMovies.removeAll(selectedItem);
        mdao.deleteMovies(selectedItem);
        movies.removeAll(selectedItem);
    }

    /**
     * Lader brugeren tilføje nye kategorier til listen af kategorier, og sender
     * det videre ned til databasen.
     *
     * @param category
     */
    public void addCategory(Category category)
    {
        categories.add(category);
        cdao.createCategory(category);
    }

    /**
     * Lader brugeren fjerne en kategori fra listen af kategorier, og sender det
     * videre ned til cdao.
     *
     * @param category
     * @throws SQLException
     */
    public void deleteCategory(Category category) throws SQLException
    {
        cdao.deleteCategory(category.getTitle());
        categories.remove(category);
    }

    /**
     * Denne metode trækker alle film ud fra databasen, og tilføjer dem til
     * listen allMovies.
     *
     * @return en observablelist af movies.
     */
    public ObservableList<Movie> getAllMovies() throws SQLException
    {
        movies.setAll(mdao.getAllMoviesFromDB());
        allMovies.addAll(movies);
        return movies;
    }

    /**
     * Den får alle kategorier fra databasen oppe i initializeren, og returnerer
     * kategorierne.
     *
     * @return en observablelist af categories.
     */
    public ObservableList<Category> getAllCategories()
    {
        return categories;
    }

    /**
     * Kalder search metoden og sender input ned i Search klassen, og setter
     * movies listen med de film den får tilbage herfra.
     *
     * @param query
     */
    public void searchMovie(String query)
    {
        globalQuery = query;
        movies.setAll(search.searchMovie(query, search.sortCategories(checkCategories, allMovies, genres)));
    }

    /**
     * Får ratingen fra den valgte film.
     *
     * @param selectedItem
     */
    public void getPersonalRatings(Movie selectedItem)
    {
        selectedItem.getRating();
        for (Movie movie1 : allMovies)
        {
            if (movie1.equals(movie))
            {
                movie.getRating();
            }
        }
    }

    /**
     * Setter movies listen ud fra de valgte kategorier i menuboxen, efter at
     * have kaldt sortCategories i Search klassen.
     *
     * @param checkCategories
     */
    public void sortCategories(List<Boolean> checkCategories)
    {
        movies.setAll(search.sortCategories(checkCategories, allMovies, genres));
        this.checkCategories = checkCategories;
    }

    /**
     * Setter genres til listen allCat.
     *
     * @param allCat
     */
    public void getChecklistCategories(List<Category> allCat)
    {
        genres = allCat;
    }
    
    /**
     * Returnerer alle categorier fundet i cdao
     * @return
     * @throws SQLException 
     */
    
    public List<Category> getAllCategorysDB() throws SQLException
    {
        return cdao.getAllCategories();
    }

    /**
     * Looper gennem listen allMovies, og hvis
     *
     * @return en film.
     */
    /**
     * Får information om filstien fra databasen, gennem readImageFromDisk i
     * mdao.
     *
     * @param image
     * @return filstien til billedet.
     */
    public BufferedImage getImage(String image) throws BLLException
    {
        try {
            return mdao.readImageFromDisk(image);
        } catch (DALException ex) {
            throw new BLLException(ex);
        }
    }

    /**
     * Metoden sætter datoen til den den nuværende dato og gennemgår alle film i
     * listen samt laver en ny liste til de film der opfylder kravene i if
     * statement. Hvis den satte dato fra i dag, er 2 år eller mere senere end
     * den sidst satte dato, og ratingen er mindre end 6, så tilføjes filmen til
     * den nye ArrayList.
     *
     * @return
     */
    public List<Movie> warning()
    {
        LocalDate date = LocalDate.now();
        List<Movie> movie = new ArrayList();
        for (Movie movy : allMovies)
        {
            LocalDate d = LocalDate.parse(movy.getLastView());
            if (date.isAfter(d.plusYears(2)) && movy.getRating() < 6)
            {
                movie.add(movy);
            }
        }
        return movie;
    }

}
