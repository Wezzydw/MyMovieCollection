/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.bll;

import com.sun.corba.se.spi.ior.Writeable;
import java.awt.image.BufferedImage;
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
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import mymoviecollection.be.Movie;
import mymoviecollection.dal.CategoryDAO;
import mymoviecollection.dal.MovieDAO;
import mymoviecollection.dal.MovieDAOTester;

/**
 *
 * @author mpoul
 */
public class Manager {

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
    private String globalQuery;

    public Manager() throws IOException {
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
    }
    
    /**
     * Deletes a movie from the DB.
     * @throws IOException 
     */
    public void deleteMovie() throws IOException {
        mdao.deleteMovies(movies);
    }
    
    /**
     * This scans folders for files. It calls itself recursively. Scans the
     * folder, if another folder is found, it steps into that, and rescans for
     * files.
     * @param filepath 
     */
    public void scanFolder(String filepath) {
        mdao.clearMovieList();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                mdao.scanFolder(filepath);
                //movies.addAll(mdao.scanFolder(filepath));
                //allMovies.addAll(mdao.scanFolder(filepath));
            }
        });
        t.start();
        movieLoop = System.currentTimeMillis();
        repeatCheckMovies();
    }
    
    /**
     * 
     */
    private void repeatCheckMovies() {
        List<Movie> tmpMovieList = new ArrayList();
        tmpMovieList.addAll(mdao.getMovie());
        
        if (initMovieLoopSize != movies.size() || initMovieLoopSize == 0) {
            initMovieLoopSize = movies.size();
            movieLoop = System.currentTimeMillis();
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                //THREAD.SLEEP FÅR HELE PROGRAMMET TIL AT LAGGE;
//                try
//                {
//                    Thread.sleep(50);
//                } catch (InterruptedException ex)
//                {
//                    Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
//                }
                if (initMovieLoopSize != movies.size() || movies.size() == 0 || System.currentTimeMillis() < movieLoop + 12000) {
                    if (mdao.getMovie().size() > 0) {
                        List<Movie> listToAdd = new ArrayList();
                        for (Movie m : tmpMovieList) {
                            if (!allMovies.contains(m)) {
                                listToAdd.add(m);

                            }
                        }
                        allMovies.addAll(listToAdd);
                        searchMovie(globalQuery);
                        try {
                            mdao.SendDataToDB(listToAdd);
                            
                            
//                        movies.add(mdao.getMovie().get(mdao.getMovie().size()-1));
//                        System.out.println("In here movie SIZE" + movies.size());
                        } catch (IOException ex) {
                            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    repeatCheckMovies();
                } //else
//                {
//
//                    allMovies.addAll(mdao.getMovie());
//                }
            }
        });
    }

    public void editMovie(Movie selectedItem) {

    }
    
    /**
     * 
     * @param category
     * @param newTitle
     * @throws SQLException 
     */
    public void editCategory(Category category, String newTitle) throws SQLException {
        categories.remove(category);
        categories.add(new Category(newTitle));
        cdao.updateCategory(category.getTitle(), newTitle);
    }
    
    /**
     * 
     * @param selectedItem 
     */
    public void playMovie(Movie selectedItem) {
        System.out.println(selectedItem.getFilePath());
        vlc.callVlc(selectedItem.getFilePath());
    }
    
    /**
     * 
     * @param selectedItem
     * @param rating 
     */
    public void sliderRateMovie(Movie selectedItem, double rating) {
        selectedItem.setRating(rating);
        //for (Movie movie1 : allMovies) {
           // if (movie1.equals(movie)) {
                //movie.setRating(rating);
                try {
                    mdao.SendRatingToDB(selectedItem);
                } catch (IOException ex) {
                    Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                }
            //}
        //}
    }
    
    /**
     * 
     * @param selectedItem
     * @throws IOException 
     */
    public void reMovie(List<Movie> selectedItem) throws IOException {
        mdao.deleteMovies(selectedItem);

        for (Movie movie1 : selectedItem) {
            movies.remove(movie1);
        }
    }
    
    /**
     * 
     * @param category 
     */
    public void addCategory(Category category) {
        categories.add(category);
        cdao.createCategory(category);
    }
    
    /**
     * 
     * @param category
     * @throws SQLException 
     */
    public void deleteCategory(Category category) throws SQLException {
        cdao.deleteCategory(category.getTitle());
        categories.remove(category);

    }
    
    /**
     * 
     * @return an observablelist of movies.
     */
    public ObservableList<Movie> getAllMovies() {
        movies.setAll(mdao.getAllMoviesFromDB());
        allMovies.addAll(movies);
        return movies;
    }
    
    /**
     * 
     * @return 
     */
    public ObservableList<Category> getAllCategories() {
        // genres.addAll(categories);
        return categories;
    }
    
    /**
     * 
     * @param query 
     */
    public void searchMovie(String query) {
        globalQuery = query;
//        movies.setAll(search.searchMovie(query, allMovies));
        movies.setAll(search.searchMovie(query, search.sortCategories(checkCategories, allMovies, genres)));
    }
    
    /**
     * 
     * @param selectedItem 
     */
    public void getPersonalRatings(Movie selectedItem) {
        selectedItem.getRating();
        for (Movie movie1 : allMovies) {
            if (movie1.equals(movie)) {
                movie.getRating();
            }
        }
    }
    
    public void onProgramClose() {
//        try {
//            mdao.SendRatingToDB(allMovies);
//        } catch (IOException ex) {
//            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
    /**
     * 
     * @param checkCategories 
     */
    public void sortCategories(List<Boolean> checkCategories) {
        System.out.println(checkCategories.size() + "hej" + genres.size() + "tonny" + categories.size());
        movies.setAll(search.sortCategories(checkCategories, allMovies, genres));
        this.checkCategories = checkCategories;
    }

    /**
     * 
     * @param allCat 
     */
    public void getChecklistCategories(List<Category> allCat) {
        genres = allCat;
    }

    /**
     * 
     * @return 
     */
    public Movie sendDataOnClick() {

        for (Movie movie1 : allMovies) {

            if (movie1.equals(movie)) {
                return movie1;
            }
        }
        return null;
    }

    /**
     * 
     */
    public void deleteHalf() {
        for (int i = 0; i < movies.size(); i++) {
            if (i % 2 == 0) {
                movies.remove(i);
            }
        }

        for (Movie m : movies) {
            System.out.println(m);
        }
    }

    /**
     * 
     * @param image
     * @return 
     */
    public BufferedImage getImage(String image)
    {
         
        return mdao.readImageFromDisk(image);
     
    }

    

}
