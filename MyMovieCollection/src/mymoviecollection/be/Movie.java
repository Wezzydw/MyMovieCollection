/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.be;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Andreas Svendsen
 */
public class Movie {

    int rating;
    int id;
    String title;
    String filePath;
    Date lastView;

    public Movie(int rating, int id, String title, String filePath, String lastView) {
        this.rating = rating;
        this.id = id;
        this.title = title;
        this.filePath = filePath;

    }
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();
    //System.out.println(dateFormat.format(date));

    public Movie(String title, String length, String releaseYear, String categori, String filepath, int id)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public int getRating() {
        return rating;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setRating(int rating) {
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

    public void setLastView(Date lastView)
    {
        this.lastView = lastView;
    }

    public String getFilePath() {
        return filePath;
    }

    public Date getLastView()
    {
        return lastView;
    }

}
