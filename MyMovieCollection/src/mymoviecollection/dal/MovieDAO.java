/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.dal;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import mymoviecollection.be.Category;
import mymoviecollection.be.Movie;

/**
 *
 * @author Andreas Svendsen
 */
public class MovieDAO {

    private List<Movie> movies;
    private int counter;
    private long startTime;
    private int requestRateTimer;
    private DatabaseConnection conProvider;
    private ImdbDAO imdb;
    private List<Movie> moviesFromDB;
    private List<Movie> oldMovieList;

    public MovieDAO() throws IOException, SQLException {
        try {
            conProvider = new DatabaseConnection();
        } catch (IOException ex) {
            throw new IOException("No database connection established " + ex);
        }

        counter = 0;
        movies = new ArrayList();
        startTime = 0;
        requestRateTimer = 11000;
        imdb = new ImdbDAO(startTime);
        moviesFromDB = getAllMoviesFromDB();
        oldMovieList = new ArrayList();
    }

    /**
     * Den kalder sig selv recursively. Den scanner en mappe igennem for
     * filer, finder den ikke nogle filer, men en anden mappe, hopper den ind og
     * scanner den nye mappe.
     *
     * @param filepath
     * @return
     */
    public List<Movie> scanFolder(String filepath) throws DALException, SQLException {
        File folder = new File(filepath);
        File[] folders = folder.listFiles();
        for (File f : folders) {
            if (f.isFile()) {
                if (checkForFileType(f) && !isAlreadyInSystem(filepath)) {
                    Movie m = getIMDBData(f.getName());
                    if (m != null) {
                        m.setFilePath(f.getAbsolutePath());
                        movies.add(m);
                    }
                }
            }
            if (f.isDirectory()) {
                scanFolder(f.getAbsolutePath());
            }
        }
        return movies;
    }

    /**
     * der bliver lavet et check på hver liste og returner enten true eller
     * false.
     *
     * @param filepath
     * @return
     */
    public boolean isAlreadyInSystem(String filepath) {
        for (Movie m : movies) {
            if (m.getFilePath() == filepath) {
                return true;
            }
        }

        for (Movie m : moviesFromDB) {
            if (m.getFilePath() == filepath) {
                return true;
            }
        }
        if (!oldMovieList.isEmpty()) {
            for (Movie m : oldMovieList) {
                if (m.getFilePath() == filepath) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Hvis Listen ikke er tom, så clear den listen
     */
    public void clearMovieList() {
        if (!movies.isEmpty()) {
            oldMovieList.addAll(movies);
        }
        movies.clear();
    }

    /**
     * returnere de film der bliver tilføjet listen løbende
     *
     * @return
     */
    public List<Movie> getMovie() {
        return movies;
    }

    /**
     * Checker for korrekt filtype
     *
     * @param f
     * @return
     */
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

    /**
     * Fungerer som et bindeled imellem alle de andre imdbMetoder, som i sidste
     * ende sender en fuldt construeret film return, ud fra en filepath
     *
     * @param filepath
     * @return
     * @throws DALException
     * @throws SQLException
     */
    private Movie getIMDBData(String filepath) throws DALException, SQLException {
        String searchResult = "";
        String idInformation = "";

        if (counter == 0) {
            startTime = System.currentTimeMillis();
            imdb.setStartTime(startTime);
        }

        while (counter > 36) {
            if (startTime + requestRateTimer < System.currentTimeMillis()) {
                counter = 0;
                startTime = System.currentTimeMillis();
                imdb.setStartTime(startTime);
            }
        }

        String searchString = imdb.makeSearchString(filepath);
        try {
            searchResult = imdb.getIMDBText(searchString);
        } catch (IOException ex) {
            throw new DALException("SearchError");
        }
        startTime = imdb.getStartTime();

        if (searchResult.contains("total_results\":0")) {
            return null;
        }

        try {
            idInformation = imdb.getIMDBText(imdb.getSearchIDQuery(searchResult));
        } catch (IOException ex) {
            throw new DALException("Could not retrieve information about : ", ex);
        }

        startTime = imdb.getStartTime();
        counter += 2;
        Movie newMovie = imdb.constructMovie(idInformation);
        LocalDate ldate = LocalDate.now();

        if (newMovie == null) {
            //Weird null error withouth this check
            return null;
        }

        newMovie.setLastView(ldate.toString());
        for (Movie m : movies) {
            if (m.getTitle().equals(newMovie.getTitle())) {
                return null;
            }
        }

        for (Movie m : oldMovieList) {
            if (m.getTitle().equals(newMovie.getTitle())) {
                return null;
            }
        }

        for (Movie m : moviesFromDB) {
            if (m.getTitle().equals(newMovie.getTitle())) {
                return null;
            }
        }
        return newMovie;
    }

    /**
     *
     * @param selectedMovie
     * @throws IOException denne metode tager fat i vores selected movies og
     * fjerner den eller dem vi ønsker at fjerne.
     */
    public void deleteMovies(List<Movie> selectedMovie) throws IOException, SQLException {
        oldMovieList.addAll(movies);
        movies.removeAll(selectedMovie);

        try (Connection con = conProvider.getConnection()) {
            String a = "DELETE FROM Movies WHERE title =?;";
            PreparedStatement prst = con.prepareStatement(a);
            for (Movie movie : selectedMovie) {
                prst.setString(1, movie.getTitle());
                prst.addBatch();
            }
            prst.executeBatch();

        } catch (SQLException ex) {
            throw new SQLException("Could not delete from db" + ex);
        }
    }

    /**
     * Denne metode laver en liste over alle vores film, så vi i programmet kan
     * se og vælge den film vi ønsker at se eller bearbejde på andre måder.
     *
     * @return
     */
    public List<Movie> getAllMoviesFromDB() throws SQLException {
        List<Movie> allMovies = new ArrayList();

        try (Connection con = conProvider.getConnection()) {
            String a = "SELECT * FROM Movies;";
            PreparedStatement prst = con.prepareStatement(a);
            ResultSet rs = prst.executeQuery();

            while (rs.next()) {
                List<String> categori = new ArrayList();
                String title = rs.getString("Title");
                String filepath = rs.getString("Filepath");
                String length = rs.getString("Length");
                int id = rs.getInt("Id");
                String releaseYear = rs.getString("ReleaseYear");
                double imdbRating = rs.getDouble("imdbRating");
                double personalRating = rs.getDouble("personalRating");
                String filePath = rs.getString("filePath");
                String lastView = rs.getString("lastView");
                String posterPath = rs.getString("posterPath");
                String genre = rs.getString("categories");
                String[] genres = genre.split(",");

                for (String s : genres) {
                    categori.add(s);
                }
                Movie movie = new Movie(title, length, releaseYear, categori, filePath, posterPath, imdbRating, personalRating, id, lastView);
                if (new File(filepath).isFile()) {
                    allMovies.add(movie);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("No data from getAllMovies" + ex);
        }
        movies.addAll(allMovies);
        return allMovies;
    }

    /**
     * Denne metode sender film filernes data til vores database, som gør det
     * muligt for os at kategorisere og filtrere i filmene.
     *
     * @param allMovies
     * @throws IOException
     */
    public void SendDataToDB(List<Movie> allMovies) throws IOException, SQLException {
        String a = "INSERT INTO Movies (title, length, imdbRating, personalRating, filePath, lastView, posterPath, releaseYear, categories ) VALUES (?,?,?,?,?,?,?,?,?);";
        try (Connection con = conProvider.getConnection()) {
            for (Movie movie : allMovies) {
                String genre = "";
                for (String s : movie.getCategory()) {
                    genre += s + ",";
                }

                PreparedStatement pstmt = con.prepareStatement(a);
                pstmt.setString(1, movie.getTitle());
                pstmt.setString(2, movie.getLength());
                pstmt.setDouble(3, movie.getImdbRating());
                pstmt.setDouble(4, movie.getRating());
                pstmt.setString(5, movie.getFilePath());
                pstmt.setString(6, movie.getLastView());
                pstmt.setString(7, movie.getPosterPath());
                pstmt.setString(8, movie.getReleaseYear());
                pstmt.setString(9, genre);
                pstmt.execute();

            }
        } catch (SQLException ex) {
            throw new SQLException("Could not save to DB" + ex);
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
    public BufferedImage readImageFromDisk(String imagePath) throws DALException {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            throw new DALException("Image not found on disk: " + imagePath, e);
        }
        return img;
    }

    /**
     * Der tages fat i vores film og søges igennem filmene efter en angivet
     * rating. Denne rating sendes til databasen så alle film der er bleven
     * ratet i løbet af programmets uptime, bliver gemt i databasen.
     *
     * @param allMovies
     * @throws IOException
     */
    public void SendRatingToDB(Movie movie) throws IOException, SQLException {
        String a = "UPDATE Movies SET personalRating = ? WHERE Title = ?;";
        try (Connection con = conProvider.getConnection()) {
            System.out.println("getRating " + movie.getRating());
            System.out.println("ID: " + movie.getId());
            PreparedStatement pstmt = con.prepareStatement(a);
            pstmt.setDouble(1, movie.getRating());
            pstmt.setString(2, movie.getTitle());
            pstmt.execute();

        } catch (SQLException ex) {
            throw new SQLException("Could not send rating to DB" + ex);
        }

    }

    /**
     * Der forbindes til databasen og sender lastview of title på movien dertil
     *
     * @param movie
     * @throws IOException
     */
    public void SendLastView(Movie movie) throws IOException, SQLException {
        String a = "UPDATE Movies SET lastView = ? WHERE Title = ?;";
        try (Connection con = conProvider.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(a);
            pstmt.setString(1, movie.getLastView());
            pstmt.setString(2, movie.getTitle());
            pstmt.execute();

        } catch (SQLException ex) {
            throw new SQLException("Could not send lastViewedTime" + ex);
        }
    }

    /**
     * der forbindes til databasen for at updatere filmene, derfor sendes alt
     * filmenenes info med
     *
     * @param movie
     */
    public void updateMovie(Movie movie) throws SQLException {
        String categories = "";
        for (String string : movie.getCategory()) {
            categories += string + ",";
        }

        String a = "UPDATE Movies SET Title = ?, length = ?, releaseYear = ?, personalRating = ?, categories = ? WHERE filepath = ?;";

        try (Connection con = conProvider.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(a);
            pstmt.setString(1, movie.getTitle());
            pstmt.setString(2, movie.getLength());
            pstmt.setString(3, movie.getReleaseYear());
            pstmt.setDouble(4, movie.getRating());
            pstmt.setString(5, categories);
            pstmt.setString(6, movie.getFilePath());
            pstmt.execute();

        } catch (SQLException ex) {
            throw new SQLException("Could not change data in DB" + ex);
        }
    }

    /**
     * returnerer categorier lavet i imdbDAO
     *
     * @return
     */
    public List<Category> getCategory() {
        return imdb.getCategorys();
    }
}
