/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.dal;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import mymoviecollection.be.Movie;

/**
 *
 * @author Andreas Svendsen
 */
public class MovieDAO
{

    List<Movie> movies;
    int counter;
    long startTime;
    int requestRateTimer;
    String requestNotFound;
    private ObservableList<Movie> movies1;
    DatabaseConnection conProvider;
    int i;
    private ImdbDAO imdb;

    public MovieDAO()
    {

        movies1 = FXCollections.observableArrayList();
        counter = 0;
        movies = new ArrayList();
        movies = new ArrayList();
        i = 0;
        try
        {
            conProvider = new DatabaseConnection();
        } catch (IOException ex)
        {
            Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        startTime = 0;
        requestRateTimer = 11000;
        requestNotFound = "The resource you requested could not be found.";
        imdb = new ImdbDAO(startTime);

    }

    /**
     *
     * @param filepath
     * @return
     */
    public List<Movie> scanFolder(String filepath)
    {
        File folder = new File(filepath);
        File[] folders = folder.listFiles();
        for (File f : folders)
        {
            if (f.isFile())
            {
                if (checkForFileType(f))
                {
                    Movie m = getIMDBData(f.getName());
                    if (m != null)
                    {
                        m.setFilePath(f.getAbsolutePath());
                        movies.add(m);
                        i++;
                    }
                }
            }

            if (f.isDirectory())
            {
                scanFolder(f.getAbsolutePath());
            }
        }
        return movies;
    }

    public void clearMovieList()
    {
        movies.clear();
    }

    public List<Movie> getMovie()
    {
        return movies;
    }

    /**
     *
     * @param f
     * @return
     */
    private boolean checkForFileType(File f)
    {
        String path = f.getAbsolutePath();
        if (path.endsWith(".mkv") || path.endsWith(".mp4")
                || path.endsWith(".mpeg4"))
        {
            if (!path.toLowerCase().contains("sample"))
            {
                return true;
            }
        }

        return false;
    }

    private Movie getIMDBData(String filepath)
    {
        String searchResult = "";
        String idInformation = "";

        if (counter == 0)
        {
            startTime = System.currentTimeMillis();
        }

        while (counter > 38)
        {
            System.out.println("spasser");
            if (startTime + requestRateTimer < System.currentTimeMillis())
            {
                counter = 0;
                startTime = System.currentTimeMillis();
            }
        }

        String searchString = imdb.makeSearchString(filepath);
        try
        {
            searchResult = imdb.getIMDBText(searchString);
        } catch (IOException ex)
        {
            Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (searchResult.contains("total_results\":0"))
        {
            System.out.println("Movie not found, please check file name");
            return null;
        }

        try
        {
            idInformation = imdb.getIMDBText(imdb.getSearchIDQuery(searchResult));
        } catch (IOException ex)
        {
            Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        counter += 2;
        return imdb.constructMovie(idInformation);
    }

    /**
     * Denne metode
     *
     * @param selectedMovie
     * @throws IOException denne metode tager fat i vores selected movies og
     * fjerner den eller dem vi ønsker at fjerne.
     */
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

    /**
     * Denne metode laver en liste over alle vores film, så vi i programmet kan
     * se og vælge den film vi ønsker at se eller bearbejde på andre måder.
     *
     * @return
     */
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
//                List<String> categori = rs.getString("Categori");
                List<String> categori = new ArrayList();
                String filepath = rs.getString("Filepath");
                String length = rs.getString("Length");
                int id = rs.getInt("Id");
                String releaseYear = rs.getString("ReleaseYear");
                //Movie herunder skal fixes
                Movie movie = new Movie(title, length, releaseYear, categori, filepath, "", -1);
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

    /**
     * Denne metode sender film filernes data til vores database, som gør det
     * muligt for os at kategorisere og filtrere i filmene.
     *
     * @param allMovies
     * @throws IOException
     */
    public void SendDataToDB(List<Movie> allMovies) throws IOException
    {
        String a = "INSERT INTO Movies (title, id, imdbRating, personalRating, filePath, lastView ) VALUES (?,?,?,?,?,?,?);";
        try (Connection con = conProvider.getConnection())
        {
            for (Movie movie : allMovies)
            {
                PreparedStatement pstmt = con.prepareStatement(a);
                pstmt.setString(1, movie.getTitle());
                pstmt.setInt(2, movie.getId());
                pstmt.setDouble(3, movie.getRating());
                pstmt.setString(4, movie.getFilePath());
                pstmt.execute();
            }
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }

    }

    /**
     * Denne metode gemmer billede filerne til vores harddisk som gør det muligt
     * at benytte disse billeder i programmet.
     *
     * @param image
     * @param title
     */
    /**
     * Billederne bliver her læst fra harddisken, og vi kan derved tilføje disse
     * billeder til de korrekte film.
     *
     * @param imagePath
     * @return
     */
    public BufferedImage readImageFromDisk(String imagePath)
    {

        BufferedImage img = null;
        try
        {
            img = ImageIO.read(new File(imagePath));
        } catch (IOException e)
        {
        }
        System.out.println(img.getHeight());
        return img;

    }

    /**
     * Der tages fat i vores film og søges igennem filmene efter en angivet
     * rating. Denne rating sendese til databasen så alle film der er bleven
     * ratet i løbet af programmets uptime, bliver gemt i databasen.
     *
     * @param allMovies
     * @throws IOException
     */
    public void SendRatingToDB(List<Movie> allMovies) throws IOException
    {
        String a = "INSERT INTO Movies (personalRating ) WHERE Id = ?;";
        try (Connection con = conProvider.getConnection())
        {
            for (Movie movie : allMovies)
            {

                PreparedStatement pstmt = con.prepareStatement(a);
                pstmt.setDouble(1, movie.getRating());
                pstmt.setInt(2, movie.getId());
                pstmt.execute();
            }
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }

    }

}
