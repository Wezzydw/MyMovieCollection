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
import java.sql.ResultSet;
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

    public MovieDAO() {
        movies = new ArrayList();
        movies = new ArrayList();
    }

    public List<Movie> scanFolder(String filepath) {

        File folder = new File(filepath);
        File[] folders = folder.listFiles();
        for (File f : folders) {
            if (f.isFile()) {
                movies.add(new Movie(1, 1, "title", "MYpath", "Test"));
            }
            if (f.isDirectory()) {
                scanFolder(f.getAbsolutePath());
                movies.addAll(scanFolder(f.getAbsolutePath()));
            }
        }
        for (Movie m : movies) {
            System.out.println(m.getTitle());
        }

        return null;
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
        
          public List<Movie> getAllMoviesFromDB()
    {
        List<Movie> allMovies = new ArrayList();
        try (Connection con = conProvider.getConnection())
        {
            String a = "SELECT * FROM Movies;";
            PreparedStatement prst = con.prepareStatement(a);
            ResultSet rs = prst.executeQuery();

            while (rs.next())
            {
                String title = rs.getString("Title");
                String categori = rs.getString("Categori");
                String filepath = rs.getString("Filepath");
                String length = rs.getString("Length");
                int id = rs.getInt("Id");
                String releaseYear = rs.getString("ReleaseYear");
                Movie movie = new Movie(title, length, releaseYear, categori, filepath, id);
                if (new File(filepath).isFile())
                {
                    allMovies.add(movie);
                }
            }
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return allMovies;
    }
}
