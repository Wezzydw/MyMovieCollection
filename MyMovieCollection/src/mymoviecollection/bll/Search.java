/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.bll;

import java.util.ArrayList;
import java.util.List;
import mymoviecollection.be.Movie;
import mymoviecollection.dal.MovieDAO;

/**
 *
 * @author Andreas Svendsen
 */
public class Search {
    private MovieDAO mdao;
    private List<Movie> movie;
    
    public Search(){
        mdao = new MovieDAO();
        movie = mdao.getAllMoviesFromDB();
    }

    public List<Movie> searchMovie(String query) {
        List<Movie> searchResult = new ArrayList();
        
        for(Movie movie1 : movie){
            int counter = 0;
            
            if(movie1.getTitle() != null && movie1.getTitle().toLowerCase().contains(query.toLowerCase())){
                if(counter == 0){
                    counter++;
                    searchResult.add(movie1);
                }
            }
        }
        return searchResult;
    }
    
    
    
    
}
