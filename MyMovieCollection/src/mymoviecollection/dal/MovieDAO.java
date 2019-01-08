/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.dal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import mymoviecollection.be.Movie;

/**
 *
 * @author Andreas Svendsen
 */
public class MovieDAO {

    List<Movie> movies;

    public MovieDAO() {
        movies = new ArrayList();
    }

    public List<Movie> scanFolder(String filepath) {

        File folder = new File(filepath);
        File[] folders = folder.listFiles();
        for (File f : folders) {
            if (f.isFile()) {
                movies.add(new Movie(1,1,"title","MYpath","Test"));
            }
            if (f.isDirectory()) {
                scanFolder(f.getAbsolutePath());
                movies.addAll(scanFolder(f.getAbsolutePath()));
            }
        }
        for(Movie m : movies)
        {
            System.out.println(m.getTitle());
        }
        
        return null;
    }

}
