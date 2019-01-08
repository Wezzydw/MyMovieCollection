/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.be;

/**
 *
 * @author Wezzy Laptop
 */
public class Rating {
    private double imdbRating;
    private double personalRating;
    private int movieId;

    public Rating(double imdbRating, double personalRating, int movieId) {
        this.imdbRating = imdbRating;
        this.personalRating = personalRating;
        this.movieId = movieId;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(double imdbRating) {
        this.imdbRating = imdbRating;
    }

    public double getPersonalRating() {
        return personalRating;
    }

    public void setPersonalRating(double personalRating) {
        this.personalRating = personalRating;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
    
    
}
