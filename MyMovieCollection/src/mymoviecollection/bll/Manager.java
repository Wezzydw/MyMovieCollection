/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.bll;

import java.io.IOException;
import java.util.List;
import mymoviecollection.be.Movie;
import mymoviecollection.dal.MovieDAO;

/**
 *
 * @author mpoul
 */
public class Manager {
    private MovieDAO mdao;
    private List<Movie> movies;
    private Movie movie;
    
    
    public Manager(){
        mdao = new MovieDAO();
        
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

    public void addCategory() {
        
    }

    public void deleteCategory() {
        
    }

    
    
    
}
