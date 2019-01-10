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
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import mymoviecollection.be.Movie;

/**
 *
 * @author Andreas Svendsen
 */
public class MovieDAO {

    List<Movie> movies;
    int counter;

    DatabaseConnection conProvider;
    int i;

    public MovieDAO() {
        counter = 0;
        movies = new ArrayList();
        movies = new ArrayList();
        i = 0;
        try {
            conProvider = new DatabaseConnection();
        } catch (IOException ex) {
            Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Movie> scanFolder(String filepath) {

        File folder = new File(filepath);
        File[] folders = folder.listFiles();
        for (File f : folders) {
            if (f.isFile()) {
                if (checkForFileType(f)) {
                    System.out.println(f.getName());

                    movies.add(getIMDBData(f.getName()));

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

    private Movie getIMDBData(String filepath) {
        String queryP1 = "https://api.themoviedb.org/3/search/movie?api_key=0c8d21c8ce1c4efd22b8bb8795427245&query=";
        String queryP2 = "";
        String queryEnd = "&include_adult=true";
        String searchString = "";
        String searchResult = "";
        String searchID = "";
        String idInformation = "";
        List<String> genreList = new ArrayList();
        long currenttime = System.currentTimeMillis();
        System.out.println("counter " + counter);
        while (counter > 17) {
            System.out.println("spasser");
            if (currenttime + 10000 < System.currentTimeMillis())
            counter = 0;
        }

        if (filepath.endsWith(".mkv")) {
            System.out.println(filepath);
            String replacedString = filepath.replace(".", " ");
            String[] split = replacedString.split(" ");
            System.out.println("replaced String " + replacedString);
            for (int i = 0; i < split.length - 1; i++) {
                if (i != 0) {

                    if (!isStringANumber(split[i])) {
                        System.out.println("Split" + split[i] + " i: " + i);
                        queryP2 += split[i] + "%20";
                    } else {
                        queryP2.substring(0, queryP2.length() - 3);
                        System.out.println("Split before break" + split[i] + " i: " + i);
                        break;
                    }
                }
                queryP2 += split[i] + "%20";

            }
            queryP2 = queryP2.substring(0, queryP2.length() - 3);
            searchString = queryP1 + queryP2 + queryEnd;

        } else if (filepath.endsWith(".mp4")) {
            searchString = queryP1 + filepath.substring(0, filepath.length() - 4) + queryEnd;
        } else if (filepath.endsWith(".mpeg4")) {
            searchString = queryP1 + filepath.substring(0, filepath.length() - 6) + queryEnd;
        }
        System.out.println("SearchString : " + searchString);
        try {
            searchResult = getIMDBText(searchString);
        } catch (IOException ex) {
            System.out.println("FUCKFUCKFUCKFUKC");
            //Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(searchResult);

        if (!searchResult.isEmpty()) {
            String[] searchResults = searchResult.split(",");
            for (String s : searchResults) {
                if (s.contains("id")) {
                    searchID = s.substring(s.indexOf(":") + 1);
                    System.out.println("searchid: " + searchID);
                    break;
                }
            }
        }
        String idString = "https://api.themoviedb.org/3/movie/" + searchID + "?api_key=0c8d21c8ce1c4efd22b8bb8795427245";

        try {
            idInformation = getIMDBText(idString);
        } catch (IOException ex) {
            Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        String title = "";
        String length = "";
        String releaseYear = "";
        String category = "ToBeDone";
        //String filepathMovie;
        //int id;

        if (!idInformation.isEmpty()) {
            String[] results = searchResult.split(",");

            //String fordetails = "https://api.themoviedb.org/3/movie/343611?api_key=0c8d21c8ce1c4efd22b8bb8795427245";
            for (String s : results) {
                if (s.matches("original_title")) {
                    title = s.substring(s.indexOf(":"));
                    System.out.println(title);
                } else if (s.matches("runtime")) {
                    length = s.substring(s.indexOf(":"));
                } else if (s.matches("release_date")) {
                    releaseYear = s.substring(s.indexOf(":"), s.indexOf(":") + 4);
                }

            }
            System.out.println(idInformation);
            String allGenres = idInformation.substring(idInformation.indexOf("genre"), idInformation.indexOf("]"));
            System.out.println(allGenres);
            String[] genre = allGenres.split("}");
            for (String s : genre) {
                genreList.add(s.substring(s.lastIndexOf(":"), s.length() - 1));
            }
        }

        Movie movie = new Movie(title, length, releaseYear, category, filepath, 999);
        System.out.println("Movie data: " + movie.getTitle() + " lenght: "
                + movie.getLength() + " releaseyear: " + movie.getReleaseYear()
                + " category: " + movie.getCategory() + " Filepath: "
                + movie.getFilePath());

        counter++;
        return movie;
    }

    private String getIMDBText(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        //add headers to the connection, or check the status if desired..

        // handle error response code it occurs
        int responseCode = connection.getResponseCode();
        InputStream inputStream;
        if (200 <= responseCode && responseCode <= 299) {
            inputStream = connection.getInputStream();
        } else {
            inputStream = connection.getErrorStream();
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        inputStream));

        StringBuilder response = new StringBuilder();
        String currentLine;

        while ((currentLine = in.readLine()) != null) {
            response.append(currentLine);
        }

        in.close();

        return response.toString();
    }

    private boolean isStringANumber(String string) {
        for (char c : string.toCharArray()) {
            if (!Character.isDigit(c)) {
                System.out.println("Checking String " + string);
                return false;
            }
        }
        return true;
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
            String findFormat = title.substring(title.lastIndexOf(".") + 1);
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
