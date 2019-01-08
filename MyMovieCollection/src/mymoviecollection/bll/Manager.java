/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.bll;

import java.io.IOException;
import java.util.List;
import mymoviecollection.be.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mymoviecollection.be.Movie;
import mymoviecollection.dal.CategoryDAO;
import mymoviecollection.dal.MovieDAO;

/**
 *
 * @author mpoul
 */
public class Manager {
    private MovieDAO mdao;
    private CategoryDAO cdao;

    private ObservableList<Movie> movies;

    private Movie movie;
    
    
    public Manager() throws IOException{
        mdao = new MovieDAO();
        cdao = new CategoryDAO();
        

        movies = FXCollections.observableArrayList();

    }
    
    public void deleteMovie() throws IOException{
        mdao.deleteMovies(movies);
    }
    
    public void scanFolder(String filepath){
        mdao.scanFolder(filepath);
    }

    public void editMovie(Movie selectedItem) {
        
    }

    public void editCategory() {
        
    }

    public void playMovie(Movie selectedItem) {
        
    }

    public void rateMovie(Movie selectedItem) {
        
    }

    public void reMovie(Movie selectedItem) {
        
    }

    public void addCategory(Category category) {
        cdao.createCategory(category);
    }

    public void deleteCategory() {
        
    }

    public ObservableList<Movie> getAllMovies() {
        return movies;
    }

    
    
    
}
