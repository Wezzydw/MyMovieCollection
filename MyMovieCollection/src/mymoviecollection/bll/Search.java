/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.bll;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.StyledEditorKit;
import mymoviecollection.be.Category;
import mymoviecollection.be.Movie;
import mymoviecollection.dal.MovieDAO;

/**
 *
 * @author Andreas Svendsen
 */
public class Search {
    private Manager manager;
    private List<Movie> movie;
    
    public Search(){
       // mdao = new MovieDAO();
       // movie = manager.getAllMovies();
    }

    public List<Movie> searchMovie(String query, List<Movie> movie) {
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
    
    public List<Movie> sortCategories(List<Boolean> checkList, List<Movie> movies, List<Category> categories){
        
        List<Movie> sortResult = new ArrayList();
        List<Category> checkTrue = new ArrayList();
        int counter = 0;
        
        for (Boolean boolean1 : checkList) {
            if(boolean1){
                checkTrue.add(categories.get(counter));
            }
            counter++;
        }
        
        for (Movie movy : movies) {
            for (String category : movy.getCategory()) {
                for (Category bæh: checkTrue){
                    if(category.equals(bæh)){
                        sortResult.add(movy);
                    }
                }
            }
            
        }
        return sortResult;
    }
    
    
}
