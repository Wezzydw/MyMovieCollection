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
        String queryEnd = "&include_adult=true&year=";
        String searchString = "";
        String year = "";

        if (filepath.endsWith(".mkv"))
        {

            String replacedString = filepath.replace(".", " ");
            String[] splitString = replacedString.split(" ");

            int lastIndex = indexOfLastYear(splitString);
            for (int i = 0; i < splitString.length - 1; i++)
            {
                if (i == 0)
                {
                    queryP2 += splitString[i];
                } else
                {
                    if (i < lastIndex)
                    {
                        queryP2 += "%20" + splitString[i];
                    } else if (isAllLetters(splitString[i]))
                    {
                        queryP2 += "%20" + splitString[i];
                    } else
                    {
                        break;
                    }
                }
            }
            year = getYearFromMovie(splitString);
            searchString = queryP1 + queryP2 + queryEnd + year;
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

    private boolean isAllLetters(String string)
    {
        for (char c : string.toCharArray())
        {
            if (!Character.isAlphabetic(c))
            {
                return false;
            }
        }

        return true;
    }

    private int amountOfYears(String string)
    {
        int amount = 0;
        for (String s : string.split("."))
        {
            if (s.length() == 4)
            {

                for (char c : s.toCharArray())
                {
                    if (!Character.isDigit(c))
                    {
                        break;
                    }
                }
                amount++;
            }
        }
        return amount;
    }

    private int indexOfLastYear(String[] string)
    {
        int index = -1;
        int counter = 0;
        for (int i = 0; i < string.length - 1; i++)
        {
            counter = 0;
            if (string[i].length() == 4)
            {
                for (char c : string[i].toCharArray())
                {
                    if (!Character.isDigit(c))
                    {
                        break;
                    }

                    counter++;
                    if (counter == 4)
                    {
                        index = i;
                    }

                }

            }

        }
        return index;
    }

    private String getYearFromMovie(String[] string)
    {
        String year = "";
        int counter = 0;
        for (int i = 0; i < string.length - 1; i++)
        {
            counter = 0;
            if (string[i].length() == 4)
            {
                for (char c : string[i].toCharArray())
                {
                    if (!Character.isDigit(c))
                    {
                        break;
                    }

                    counter++;
                    if (counter == 4)
                    {
                        year = string[i];
                    }

                }

            }

        }
        return year;
    }

    private boolean isStringANumber(String string)
    {
        for (char c : string.toCharArray())
        {
            if (!Character.isDigit(c))
            {
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

        File outputfile = new File("images/" + title);
        if (!outputfile.exists())
        {
            try
            {
                BufferedImage bi = image;

                //File outputfile = new File("images/" + title);

                String findFormat = title.substring(title.lastIndexOf(".") + 1);
                ImageIO.write(bi, findFormat, outputfile);

            } catch (IOException e)
            {

            }
        }
        return outputfile.getAbsolutePath();
    }
}
