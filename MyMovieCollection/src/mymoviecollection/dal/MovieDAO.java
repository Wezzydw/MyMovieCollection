/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.dal;

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
       DatabaseConnection conProvider;
        List<Movie> movies;

    
    public MovieDAO() throws IOException{
    conProvider = new DatabaseConnection();
    movies = new ArrayList();
    
    }
        public void deleteMovies(List<Movie> selectedMovie) throws IOException
    {
        try (Connection con = conProvider.getConnection())
        {
            String a = "DELETE FROM Movies WHERE Id =?;";
            PreparedStatement prst = con.prepareStatement(a);
            for (Movie movie : movies)
            {
                prst.setInt(1, movie.getId());
                prst.addBatch();
            }
            prst.executeBatch();

        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }
}
