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
       
    }
    
    /**
     * Denne metode looper gennem input fra brugeren, og ser om film-titlerne
     * indholder søgeordet query.
     * @param query
     * @param movie
     * @return en liste af film der indholder søgeordet.
     */
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
    
    /**
     * Denne metode sorterer listen film, efter hvilke kategorier der er tjekket
     * af i menuboxen. Den tjekker også kategorierne for om de er tjekket af
     * eller ej.
     * @param checkList
     * @param movies
     * @param categories
     * @return sortResult.
     */
    public List<Movie> sortCategories(List<Boolean> checkList, List<Movie> movies, List<Category> categories) {

        List<Movie> sortResult = new ArrayList();
        List<Category> checkTrue = new ArrayList();
        int counter = 0;
        if (checkList == null) {
            return movies;
        }
        for (Boolean boolean1 : checkList) {
            if (boolean1) {
                checkTrue.add(categories.get(counter));
            }
            counter++;
        }
        System.out.println(checkTrue.size());
        if (checkTrue.size() == 0) {
            return movies;
        }
        for (Movie movy : movies) {
            int c1 = 0;
            for (String category : movy.getCategory()) {
                for (Category bæh : checkTrue) {
                    System.out.println(movy.getTitle() + bæh + category);
                    int c2 = 0;
                    if (category.equals(bæh.toString())) {
                        c1++;
                        if (c1 == checkTrue.size()) {
                            sortResult.add(movy);
                        }
                    }
                }
            }
        }
        return sortResult;
    }

}
