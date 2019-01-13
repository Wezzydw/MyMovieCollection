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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import mymoviecollection.be.Movie;

/**
 *
 * @author Wezzy
 */
public class ImdbDAO
{

    long startTime;
    int requestRateTimer;

    public ImdbDAO(long startTime)
    {
        this.startTime = startTime;
        requestRateTimer = 11000;
    }

    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public String makeSearchString(String filepath)
    {
        String queryP1 = "https://api.themoviedb.org/3/search/movie?api_key=0c8d21c8ce1c4efd22b8bb8795427245&query=";
        String queryP2 = "";
        String queryEnd = "&include_adult=true";
        String searchString = "";

        if (filepath.endsWith(".mkv"))
        {
            System.out.println(filepath);
            String replacedString = filepath.replace(".", " ");
            String[] split = replacedString.split(" ");
            System.out.println("replaced String " + replacedString);

            for (int i = 0; i < split.length - 1; i++)
            {
                if (i != 0)
                {

                    if (!isStringANumber(split[i]))
                    {
                        System.out.println("Split" + split[i] + " i: " + i);
                        queryP2 += split[i] + "%20";
                    } else if (isStringANumber(split[i]) && isStringANumber(split[i + 1]))
                    {
                        queryP2 += split[i] + "%20";
                    } else
                    {
                        queryP2.substring(0, queryP2.length() - 3);
                        System.out.println("Split before break" + split[i] + " i: " + i);
                        break;
                    }
                }
                queryP2 += split[i] + "%20";

            }
            queryP2 = queryP2.substring(0, queryP2.length() - 3);
            searchString = queryP1 + queryP2 + queryEnd;

        } else if (filepath.endsWith(".mp4"))
        {
            searchString = queryP1 + filepath.substring(0, filepath.length() - 4) + queryEnd;
            searchString = searchString.replace(" ", "%20");
        } else if (filepath.endsWith(".mpeg4"))
        {
            searchString = queryP1 + filepath.substring(0, filepath.length() - 6) + queryEnd;
            searchString = searchString.replace(" ", "%20");
        }

        return searchString;
    }

    private boolean isStringANumber(String string)
    {
        for (char c : string.toCharArray())
        {
            if (!Character.isDigit(c))
            {
                System.out.println("Checking String " + string);
                return false;
            }
        }
        return true;
    }

    public String getIMDBText(String url) throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        //add headers to the connection, or check the status if desired..

        // handle error response code it occurs
        int responseCode = connection.getResponseCode();
        InputStream inputStream;
        if (200 <= responseCode && responseCode <= 299)
        {
            inputStream = connection.getInputStream();
        } else
        {
            inputStream = connection.getErrorStream();
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        inputStream));

        StringBuilder response = new StringBuilder();
        String currentLine;

        while ((currentLine = in.readLine()) != null)
        {
            response.append(currentLine);
        }

        in.close();
        if (response.toString().contains("status_code\":25"))
        {
            startTime = System.currentTimeMillis();
            while (startTime + requestRateTimer > System.currentTimeMillis())
            {
                //System.out.println("Spasser2");
                //System.out.println((startTime + requestRateTimer) + " " + System.currentTimeMillis() + " " + (startTime + requestRateTimer - System.currentTimeMillis()));
            }
            //System.out.println(url);
            return getIMDBText(url);
        }
        return response.toString();
    }

    public String getSearchIDQuery(String searchResult)
    {
        String searchID = "";
        String searchP1 = "https://api.themoviedb.org/3/movie/";
        String serachP2 = "?api_key=0c8d21c8ce1c4efd22b8bb8795427245";
        if (!searchResult.isEmpty())
        {
            String[] searchResults = searchResult.split(",");
            for (String s : searchResults)
            {
                if (s.contains("id"))
                {
                    searchID = s.substring(s.indexOf(":") + 1);
                    System.out.println("searchid: " + searchID);
                    break;
                }
            }
        }

        String idString = searchP1 + searchID + serachP2;
        return idString;
    }
    
    public Movie constructMovie(String information)
    {
        String title = "";
        String length = "";
        String releaseYear = "";
        double imdbRating = -1;
        String posterPathOnline = "";
        String posterPath = "";
        List<String> genreList = new ArrayList();
        String posterURLp1 = "https://image.tmdb.org/t/p/original/";
        

        if (!information.isEmpty())
        {
            String[] results = information.split(",");
            for (String s : results)
            {
                if (s.contains("original_title"))
                {
                    title = s.substring(s.indexOf(":") + 2, s.length() - 1);
                } else if (s.contains("release_date"))
                {
                    releaseYear = s.substring(s.indexOf(":") + 2, s.indexOf(":") + 6);
                } else if (s.contains("runtime"))
                {
                    length = s.substring(s.indexOf(":") + 1);
                } else if (s.contains("vote_average"))
                {
                    imdbRating = Double.parseDouble(s.substring(s.indexOf(":") + 1));
                } else if (s.contains("poster_path"))
                {
                    posterPathOnline = s.substring(s.indexOf(":") + 2, s.length() - 1);
                }
            }
            
            
            BufferedImage bi = null;
            URL url = null;

            try
            {
                url = new URL(posterURLp1 + posterPathOnline);
            } catch (MalformedURLException ex)
            {
                Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            try
            {
                bi = ImageIO.read(url);
            } catch (IOException ex)
            {
                Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            posterPath = saveImageToDisk(bi, title + posterPathOnline.substring(posterPathOnline.length() - 4));
            
            

            String allGenres = information.substring(information.indexOf("genre"), information.indexOf("]"));
            if (allGenres.contains("}"))
            {
                String[] genre = allGenres.split("}");

                for (String s : genre)
                {
                    System.out.println("GenreList sout " + s);
                    genreList.add(s.substring(s.lastIndexOf(":") + 2, s.length() - 1));
                }
            } else
            {
                genreList.add("NOT A CATEGORY, NOT SURE IF WE CAN HANDLE NULL/EMPTY");
            }
        }

        Movie movie = new Movie(title, length, releaseYear, genreList, "", "", imdbRating);

        return movie;
        
    }
    
    public String saveImageToDisk(BufferedImage image, String title)
    {
        title = title.replace(":", "_");
        title = title.replace("/", "_");
        title = title.replace("\\", "_");
        title = title.replace("*", "_");
        title = title.replace("?", "_");
        title = title.replace("\"", "_");
        title = title.replace("<", "_");
        title = title.replace(">", "_");
        title = title.replace("|", "_");
        
        try
        {
            BufferedImage bi = image;
            System.out.println("images/" + title);
            File outputfile = new File("images/" + title);
            String findFormat = title.substring(title.lastIndexOf(".") + 1);
            ImageIO.write(bi, findFormat, outputfile);
            
        } catch (IOException e)
        {
            
        }
        return "images/" + title;
    }
}
