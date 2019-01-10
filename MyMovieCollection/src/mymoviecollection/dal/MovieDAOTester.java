/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.dal;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import mymoviecollection.be.Movie;
import mymoviecollection.bll.Search;

/**
 *
 * @author Wezzy Laptop
 */
public class MovieDAOTester {
    
    List<Movie> movies;
    String title;
    String releaseYear = "";
    String categori = "";
    String filepath = "";
    String length = "";
    int id = 0;
    
    public MovieDAOTester()
    {
        movies = new ArrayList();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //File f = new File(uri);
           MovieDAO m = new MovieDAO();
           Search s = new Search();
//        for (Movie mv : m.scanFolder("\\\\WEZZY\\FILM"))
//        {
//            System.out.println(mv.getTitle());
//        }
        String imagePath = "images/møde.jpg";
        BufferedImage i = m.readImageFromDisk(imagePath);
        System.out.println(i);
        m.saveImageToDisk(i,"tonny1.png");
     
    }
    
        public List<Movie> scanFolder(String filepath) {

        File folder = new File(filepath);
        File[] folders = folder.listFiles();
        int i = 0;
        for (File f : folders) {
            if (f.isFile()) {
                if (checkForFileType(f)) {
                    System.out.println("f.getname " + f.getName());
                    Movie a = new Movie(f.getName(), "", "", "", "", 1);
                    System.out.println(" print a " + a.getTitle());
                    movies.add(a);

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
        
        
        
        
}
