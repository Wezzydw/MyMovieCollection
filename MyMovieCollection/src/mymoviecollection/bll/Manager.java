/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.bll;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
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
    private Search search;
    
    
    public Manager() throws IOException{
        mdao = new MovieDAO();
        cdao = new CategoryDAO();
        search = new Search();
        

        movies = FXCollections.observableArrayList();
        categories = FXCollections.observableArrayList();
        categories.addAll(cdao.getAllCategories());
    }
    
    public void deleteMovie() throws IOException{
        mdao.deleteMovies(movies);
    }
    
    public void scanFolder(String filepath){
        
        mdao.scanFolder(filepath);
    }

    public void editMovie(Movie selectedItem) {
        
    }

    public void editCategory(Category category, String newTitle) throws SQLException {
        categories.remove(category);
        categories.add(new Category(newTitle));
        cdao.updateCategory(category.getTitle(), newTitle);
    }

    public void playMovie(Movie selectedItem) {
        
    }

    public void sliderRateMovie(Movie selectedItem, double rating) {
        selectedItem.setRating(rating);
    }

    public void reMovie(Movie selectedItem) {
        
    }

    public String addCategory(Category category) {
        categories.add(category);
        return cdao.createCategory(category);
    }

    public void deleteCategory(Category category) throws SQLException {
        cdao.deleteCategory(category.getTitle());
        categories.remove(category);
        
    }

    public ObservableList<Movie> getAllMovies() {
        MovieDAOTester md = new MovieDAOTester();
        movies.addAll(md.scanFolder("\\\\WEZZY\\FILM"));
        for (Movie m : movies)
        {
            System.out.println(m.getTitle());
        }
        //movies.addAll(mdao.scanFolder("\\\\WEZZY\\FILM"));
        return movies;
    }

    public ObservableList<Category> getAllCategories()
    {
        return categories;
    }

    public void searchMovie(String query) {
        movies.setAll(search.searchMovie(query));
        System.out.println("test " + search.searchMovie(query).size());
        for (Movie m : search.searchMovie(query))
        {
            System.out.println(m.getTitle());
        }
        
    }

   

    
    
    
}
