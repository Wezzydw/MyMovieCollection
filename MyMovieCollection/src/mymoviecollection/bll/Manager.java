/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.bll;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import mymoviecollection.be.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mymoviecollection.be.Movie;
import mymoviecollection.dal.CategoryDAO;
import mymoviecollection.dal.MovieDAO;
import mymoviecollection.dal.MovieDAOTester;

/**
 *
 * @author mpoul
 */
public class Manager
{

    private MovieDAO mdao;
    private CategoryDAO cdao;
    private ObservableList<Movie> movies;
    private ObservableList<Category> categories;
    private Movie movie;
    private double sliderRating;
    private Player vlc;
    private Search search;
    private List<Movie> allMovies;
    private List<Category> genres;
    private List<Boolean> checkCategories;
    private long movieLoop;
    private int initMovieLoopSize;

    public Manager() throws IOException
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
    }

    public void deleteMovie() throws IOException
    {
        mdao.deleteMovies(movies);
    }

    public void scanFolder(String filepath)
    {
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                movies.addAll(mdao.scanFolder(filepath));
                allMovies.addAll(movies);
            }
        });
        t.start();
        repeatCheckMovies();
    }

    private void repeatCheckMovies()
    {

        if (initMovieLoopSize != movies.size() || initMovieLoopSize == 0)
        {
            initMovieLoopSize = movies.size();
            movieLoop = System.currentTimeMillis();
        }

        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(200);
                } catch (InterruptedException ex)
                {
                    Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (initMovieLoopSize != movies.size() || movies.size() == 0 || System.currentTimeMillis() < movieLoop + 10000)
                {
                    repeatCheckMovies();
                }
            }
        });
        movies.setAll(mdao.getMovie());
    }

    public void editMovie(Movie selectedItem)
    {

    }

    public void editCategory(Category category, String newTitle) throws SQLException
    {
        categories.remove(category);
        categories.add(new Category(newTitle));
        cdao.updateCategory(category.getTitle(), newTitle);
    }

    public void playMovie(Movie selectedItem)
    {
        System.out.println(selectedItem.getFilePath());
        vlc.callVlc(selectedItem.getFilePath());
    }

    public void sliderRateMovie(Movie selectedItem, double rating)
    {
        selectedItem.setRating(rating);
        for (Movie movie1 : allMovies)
        {
            if (movie1.equals(movie))
            {
                movie.setRating(rating);
            }
        }
    }

    public void reMovie(List<Movie> selectedItem) throws IOException
    {
        mdao.deleteMovies(selectedItem);

        for (Movie movie1 : selectedItem)
        {
            movies.remove(movie1);
        }
    }

    public void addCategory(Category category)
    {
        categories.add(category);
        cdao.createCategory(category);
    }

    public void deleteCategory(Category category) throws SQLException
    {
        cdao.deleteCategory(category.getTitle());
        categories.remove(category);

    }

    public ObservableList<Movie> getAllMovies()
    {
        MovieDAOTester md = new MovieDAOTester();
        //movies.addAll(mdao.scanFolder("\\\\WEZZY\\FILM"));
        for (Movie m : movies)
        {
            System.out.println(m.getTitle());
        }
        //movies.addAll(mdao.scanFolder("\\\\WEZZY\\FILM"));
        allMovies.addAll(movies);
        return movies;
    }

    public ObservableList<Category> getAllCategories()
    {
        // genres.addAll(categories);
        return categories;
    }

    public void searchMovie(String query)
    {
        System.out.println("Size" + search.searchMovie(query, allMovies).size());
        System.out.println("Allmovies" + allMovies.size());
//        movies.setAll(search.searchMovie(query, allMovies));
        movies.setAll(search.searchMovie(query, search.sortCategories(checkCategories, allMovies, genres)));
    }

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

    public void onProgramClose()
    {
        try
        {
            mdao.SendRatingToDB(allMovies);
        } catch (IOException ex)
        {
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sortCategories(List<Boolean> checkCategories)
    {
        System.out.println(checkCategories.size() + "hej" + genres.size() + "tonny" + categories.size());
        movies.setAll(search.sortCategories(checkCategories, allMovies, genres));
        this.checkCategories = checkCategories;
    }

    public void getChecklistCategories(List<Category> allCat)
    {
        genres = allCat;
    }

    public Movie sendDataOnClick()
    {

        for (Movie movie1 : allMovies)
        {

            if (movie1.equals(movie))
            {
                return movie1;
            }
        }
        return null;
    }

    public void deleteHalf()
    {
        for (int i = 0; i < movies.size(); i++)
        {
            if (i % 2 == 0)
            {
                movies.remove(i);
            }
        }

        for (Movie m : movies)
        {
            System.out.println(m);
        }
    }

}
