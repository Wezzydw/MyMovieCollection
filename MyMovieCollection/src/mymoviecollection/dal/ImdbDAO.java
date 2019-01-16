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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import mymoviecollection.be.Category;
import mymoviecollection.be.Movie;

/**
 *
 * @author Wezzy
 */
public class ImdbDAO {

    long startTime;
    private static final String queryP1 = "https://api.themoviedb.org/3/search/movie?api_key=0c8d21c8ce1c4efd22b8bb8795427245&query=";
    private static final String queryEnd = "&include_adult=true&year=";
    private static final int requestRateTimer = 11000;
    private static final String searchP1 = "https://api.themoviedb.org/3/movie/";
    private static final String serachP2 = "?api_key=0c8d21c8ce1c4efd22b8bb8795427245";
    private static final String posterURLp1 = "https://image.tmdb.org/t/p/original/";
    private CategoryDAO d;

    public ImdbDAO(long startTime) throws IOException {
        this.startTime = startTime;
        d = new CategoryDAO();
    }
/**
 * Sætter en starttime som er udfra variablen man putter ind.
 * @param startTime 
 */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
/**
 * @return returnere den satte starttime.
 */
    public long getStartTime() {
        return startTime;
    }

/**
 * metoden tager en filepath som input og prøver så vidt muligt at trække de
 * relevante informationer ud af den. Den benytter forskellige algoritmer efter 
 * hvilken filtype den bearbejder.
 * @param filepath
 * @return 
 */

    public String makeSearchString(String filepath) {
        String queryP2 = "";
        String searchString = "";
        String year = "";

        if (filepath.endsWith(".mkv")) {

            String replacedString = filepath.replace(".", " ");
            String[] splitString = replacedString.split(" ");

            int lastIndex = indexOfLastYear(splitString);
            for (int i = 0; i < splitString.length - 1; i++) {
                if (i == 0) {
                    queryP2 += splitString[i];
                } else {
                    if (i < lastIndex) {
                        queryP2 += "%20" + splitString[i];
                    } else if (isAllLetters(splitString[i])) {
                        queryP2 += "%20" + splitString[i];
                    } else {
                        break;
                    }
                }
            }
            year = getYearFromMovie(splitString);
            searchString = queryP1 + queryP2 + queryEnd + year;
        } else if (filepath.endsWith(".mp4")) {
            searchString = queryP1 + filepath.substring(0, filepath.length() - 4) + queryEnd;
            searchString = searchString.replace(" ", "%20");
        } else if (filepath.endsWith(".mpeg4")) {
            searchString = queryP1 + filepath.substring(0, filepath.length() - 6) + queryEnd;
            searchString = searchString.replace(" ", "%20");
        }
        return searchString;
    }
/**
 * metoden sørger for at string kun indeholder bogstaver.
 * @param string
 * @return 
 */
    private boolean isAllLetters(String string) {
        for (char c : string.toCharArray()) {
            if (!Character.isAlphabetic(c)) {
                return false;
            }
        }
        return true;
    }
/**
 * metoden finder indexet fra de sidste år der i stringArrayet.
 * @param string
 * @return index
 */
    private int indexOfLastYear(String[] string) {
        int index = -1;
        int counter = 0;
        for (int i = 0; i < string.length - 1; i++) {
            counter = 0;
            if (string[i].length() == 4) {
                for (char c : string[i].toCharArray()) {
                    if (!Character.isDigit(c)) {
                        break;
                    }
                    counter++;
                    if (counter == 4) {
                        index = i;
                    }
                }
            }
        }
        return index;
    }
/**
 * Metoden retunere filmens produkktions år, ved at stringen ser bort fra characters
 * og kun søger efter 4 cifret tal.
 * @param string
 * @return year
 */
    private String getYearFromMovie(String[] string) {
        String year = "";
        int counter = 0;
        for (int i = 0; i < string.length - 1; i++) {
            counter = 0;
            if (string[i].length() == 4) {
                for (char c : string[i].toCharArray()) {
                    if (!Character.isDigit(c)) {
                        break;
                    }
                    counter++;
                    if (counter == 4) {
                        year = string[i];
                    }
                }
            }
        }
        return year;
    }
/**
 * 
 * @param url
 * @return
 * @throws IOException
 * @throws DALException 
 */
    public String getIMDBText(String url) throws IOException, DALException {
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
        if (response.toString().contains("status_code\":25")) {
            startTime = System.currentTimeMillis();
            while (startTime + requestRateTimer > System.currentTimeMillis()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    throw new DALException("Thread.sleep() error + " + response.toString());
                }
            }
            return getIMDBText(url);
        }
        return response.toString();
    }
/**
 * metoden tager et imdb id udfra dens søgeresultat.
 * @param searchResult
 * @return 
 */
    public String getSearchIDQuery(String searchResult) {
        String searchID = "";

        if (!searchResult.isEmpty()) {
            String[] searchResults = searchResult.split(",");
            for (String s : searchResults) {
                if (s.contains("id")) {
                    searchID = s.substring(s.indexOf(":") + 1);
                    break;
                }
            }
        }

        String idString = searchP1 + searchID + serachP2;
        return idString;
    }
/**
 * metoden bliver givet en streng af information fra imdb af, så konsturere den 
 * et image og en imagepath. Der laves så en ny movie udfra alt den data.
 * @param information
 * @return
 * @throws DALException 
 */
    public Movie constructMovie(String information) throws DALException, SQLException {
        String title = "";
        String length = "";
        String releaseYear = "";
        double imdbRating = -1;
        String posterPathOnline = "";
        String posterPath = "";
        List<String> genreList = new ArrayList();

        if (!information.isEmpty()) {
            String[] results = information.split(",");
            for (String s : results) {
                if (s.contains("original_title")) {
                    title = s.substring(s.indexOf(":") + 2, s.length() - 1);
                } else if (s.contains("release_date")) {
                    releaseYear = s.substring(s.indexOf(":") + 2, s.indexOf(":") + 6);
                } else if (s.contains("runtime")) {
                    length = s.substring(s.indexOf(":") + 1);
                } else if (s.contains("vote_average")) {
                    imdbRating = Double.parseDouble(s.substring(s.indexOf(":") + 1));
                } else if (s.contains("poster_path")) {
                    posterPathOnline = s.substring(s.indexOf(":") + 2, s.length() - 1);
                }
            }

            BufferedImage bi = null;
            URL url = null;

            try {
                url = new URL(posterURLp1 + posterPathOnline);
            } catch (MalformedURLException ex) {
                return null;
            }

            try {
                System.out.println(title + " " + url.toString());
                bi = ImageIO.read(url);
            } catch (IOException ex) {
                return null;
            }
            posterPath = saveImageToDisk(bi, title + posterPathOnline.substring(posterPathOnline.length() - 4));
            String allGenres = information.substring(information.indexOf("genre"), information.indexOf("]"));
            if (allGenres.contains("}")) {
                String[] genre = allGenres.split("}");

                List<Category> categories = d.getAllCategories();
                List<String> list = new ArrayList();
                List<String> toAdd = new ArrayList();

                for (Category c : categories) {
                    list.add(c.getTitle());
                }

                for (String s : genre) {
                    genreList.add(s.substring(s.lastIndexOf(":") + 2, s.length() - 1));

                }

                for (String s : genreList) {
                    int counter = 0;
                    for (String k : list) {
                        if (!k.equals(s)) {
                            counter++;
                            if (counter == list.size()) {
                                toAdd.add(s);
                            }
                        }

                    }
                }
                for (String s : toAdd) {
                    d.createCategory(new Category(s));                }

            } else {
                //throw new DALException("Movie has no categories" + title);
                return null;
            }
        }

        Movie movie = new Movie(title, length, releaseYear, genreList, "", posterPath, imdbRating);
        return movie;
    }

    /**
     * Denne metode gemmer billede filerne til vores harddisk som gør det muligt
     * at benytte disse billeder i programmet.
     *
     * @param image
     * @param title
     * @return
     */
    public String saveImageToDisk(BufferedImage image, String title) throws DALException {
        title = title.replace(":", "_");
        title = title.replace("/", "_");
        title = title.replace("\\", "_");
        title = title.replace("*", "_");
        title = title.replace("?", "_");
        title = title.replace("\"", "_");
        title = title.replace("<", "_");
        title = title.replace(">", "_");
        title = title.replace("|", "_");

        File outputfile = new File("images/" + title);
        if (!outputfile.exists()) {
            try {
                BufferedImage bi = image;
                String findFormat = title.substring(title.lastIndexOf(".") + 1);
                ImageIO.write(bi, findFormat, outputfile);
            } catch (IOException e) {
                throw new DALException("Could not save image: " + title);
            }
        }
        return outputfile.getAbsolutePath();
    }
}
