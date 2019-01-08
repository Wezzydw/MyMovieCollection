/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.dal;

import java.io.File;
import mymoviecollection.be.Movie;

/**
 *
 * @author Wezzy Laptop
 */
public class MovieDAOTester {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //File f = new File(uri);
        MovieDAO m = new MovieDAO();
        for (Movie mv : m.scanFolder("\\\\WEZZY\\FILM"))
        {
            System.out.println(mv.getTitle());
        }
    }
    
}
