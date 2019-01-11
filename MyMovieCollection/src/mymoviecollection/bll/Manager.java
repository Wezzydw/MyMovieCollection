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


    
    
    public Manager() throws IOException{
        mdao = new MovieDAO();
        cdao = new CategoryDAO();
        search = new Search();
        allMovies = new ArrayList();
        

        vlc = new Player();

        movies = FXCollections.observableArrayList();
        categories = FXCollections.observableArrayList();
        categories.addAll(cdao.getAllCategories());
    }
    
    public void deleteMovie() throws IOException{
        mdao.deleteMovies(movies);
    }
    
    public void scanFolder(String filepath){        
        
        movies.addAll(mdao.scanFolder(filepath));
        allMovies.addAll(movies);
    }

    public void editMovie(Movie selectedItem) {
        
    }

    public void editCategory(Category category, String newTitle) throws SQLException {
        categories.remove(category);
        categories.add(new Category(newTitle));
        cdao.updateCategory(category.getTitle(), newTitle);
    }

    public void playMovie(Movie selectedItem) {
        System.out.println(selectedItem.getFilePath());
         vlc.callVlc(selectedItem.getFilePath());
    }

    public void sliderRateMovie(Movie selectedItem, double rating) {
        selectedItem.setRating(rating);
        for (Movie movie1 : allMovies) {
            if(movie1.equals(movie)){
                movie.setRating(rating);
            }
        }
    }

    public void reMovie(Movie selectedItem) {
        
    }

    public void addCategory(Category category) {
        categories.add(category);
        cdao.createCategory(category);
    }

    public void deleteCategory(Category category) throws SQLException {
        cdao.deleteCategory(category.getTitle());
        categories.remove(category);
        
    }

    public ObservableList<Movie> getAllMovies() {
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
        return categories;
    }

    public void searchMovie(String query) {
        System.out.println("Size" +search.searchMovie(query, allMovies).size());
        System.out.println("Allmovies" + allMovies.size());
        movies.setAll(search.searchMovie(query, allMovies));        
    }

    public void getPersonalRatings(Movie selectedItem) {
        selectedItem.getRating();
        for (Movie movie1 : allMovies) {
            if(movie1.equals(movie)){
                movie.getRating();
            }
        }
    }

    public void onProgramClose() {
        try {
            mdao.SendRatingToDB(allMovies);
        } catch (IOException ex) {
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

   

    
    
    
}
