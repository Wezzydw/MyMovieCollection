/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.dal;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
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
                if (checkForFileType(f)) {
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

    private boolean checkForFileType(File f) {
        String path = f.getAbsolutePath();
        if (path.endsWith(".mkv") || path.endsWith(".mp4")
                || path.endsWith(".mpeg4")) {
            if (!path.toLowerCase().contains("sample")) {
                return true;
            }
        }

        return false;
    }

    private void getIMDBData(String filepath) {

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

    public List<Movie> getAllMoviesFromDB() {
        List<Movie> allMovies = new ArrayList();
        try (Connection con = conProvider.getConnection()) {
            String a = "SELECT * FROM Movies;";
            PreparedStatement prst = con.prepareStatement(a);
            ResultSet rs = prst.executeQuery();

            while (rs.next()) {
                String title = rs.getString("Title");
                String categori = rs.getString("Categori");
                String filepath = rs.getString("Filepath");
                String length = rs.getString("Length");
                int id = rs.getInt("Id");
                String releaseYear = rs.getString("ReleaseYear");
                Movie movie = new Movie(title, length, releaseYear, categori, filepath, id);
                if (new File(filepath).isFile()) {
                    allMovies.add(movie);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return allMovies;
    }

    public void SendDataToDB(List<Movie> allMovies) throws IOException {
        String a = "INSERT INTO Movies (Title, Categori, Filepath, Length, ReleaseYear) VALUES (?,?,?,?,?,?,?);";
        try (Connection con = conProvider.getConnection()) {
            for (Movie movie : allMovies) {
                PreparedStatement pstmt = con.prepareStatement(a);
                pstmt.setString(1, movie.getTitle());
                //pstmt.setString(4, movie.getCategori());
                pstmt.setString(4, movie.getCategory());
                pstmt.setString(5, movie.getFilePath());
                pstmt.setString(6, movie.getLength());
                pstmt.setString(7, movie.getReleaseYear());
                pstmt.execute();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public void saveImageToDisk(BufferedImage image, String title) {
        try {
            BufferedImage bi = image;
            File outputfile = new File("images/" + title);
            String findFormat = title.substring(title.lastIndexOf(".")+1);
            ImageIO.write(bi, findFormat, outputfile);
        } catch (IOException e) {
            
        }
    }

    public BufferedImage readImageFromDisk(String imagePath) {

        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
        }
        System.out.println(img.getHeight());
        return img;

    }

}
