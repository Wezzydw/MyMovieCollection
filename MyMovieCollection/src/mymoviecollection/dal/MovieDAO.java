/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.dal;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mymoviecollection.be.Movie;

/**
 *
 * @author Andreas Svendsen
 */
public class MovieDAO {

    List<Movie> movies;

    DatabaseConnection conProvider;
    int i;

    public MovieDAO() {
        movies = new ArrayList();
        movies = new ArrayList();
        i = 0;
    }

    public List<Movie> scanFolder(String filepath) {

        File folder = new File(filepath);
        File[] folders = folder.listFiles();
        for (File f : folders) {
            if (f.isFile()) {
                if(f.getAbsolutePath().endsWith(".mkv") && !f.getAbsolutePath().toLowerCase().contains("sample"))
                {
                    movies.add(new Movie(1, 1, "title: " + i + " " + f.getName(), "MYpath", "Test"));
                i++;
                }
                
            }
            
            if (f.isDirectory()) {
                System.out.println(f.getAbsolutePath());
                scanFolder(f.getAbsolutePath());
                //movies.addAll(scanFolder(f.getAbsolutePath()));
                //i++;
            }
        }
        System.out.println(movies.size());
//        for (Movie m : movies) {
//            System.out.println(m.getTitle());
//        }

        return movies;
    }


    public void deleteMovies(List<Movie> selectedMovie) throws IOException {
        try (Connection con = conProvider.getConnection()) {
            String a = "DELETE FROM Movies WHERE Id =?;";
            PreparedStatement prst = con.prepareStatement(a);
            for (Movie movie : movies) {
                prst.setInt(1, movie.getId());
                prst.addBatch();
            }
            prst.executeBatch();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
