/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.be;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Andreas Svendsen
 */
public class Movie {

    double rating;
    int id;
    String title;
    String filePath;
    String lastView;
    String length;
    String releaseYear;
    List<String> categories;
    String posterPath;
    double imdbRating;
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    String posterURL;
    //Date date = new Date();

    public Movie(double rating, int id, String title, String filePath, String lastView) {
        this.rating = rating;
        this.id = id;
        this.title = title;
        this.filePath = filePath;
    }

    //System.out.println(dateFormat.format(date));
    public Movie(String title, String length, String releaseYear, List<String> categories, String filepath, String posterPath, double imdbRating) {

        this.title = title;
        this.filePath = filepath;
        this.length = length;
        this.releaseYear = releaseYear;
        this.categories = categories;
        this.posterPath = posterPath;
        this.imdbRating = imdbRating;
    }

    public Movie(String title, String length, String releaseYear, List<String> categories, String filepath, String posterPath, double imdbRating, double personalRating, int id, String lastView) {
        this.title = title;
        this.length = length;
        this.releaseYear = releaseYear;
        this.categories = categories;
        this.filePath = filepath;
        this.posterPath = posterPath;
        this.imdbRating = imdbRating;
        this.rating = personalRating;
        this.id = id;
        this.lastView = this.lastView;
    }

    public double getRating() {
        return rating;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setLastView(String lastView) {
        this.lastView = lastView;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getLastView() {
        return lastView;
    }

    public String toString() {
        return title;
    }

    public List<String> getCategory() {
        return categories;
    }

    public String getLength() {
        return length;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public String getPosterPath() {
        return posterPath;
    }
    

}
