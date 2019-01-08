/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.dal;

/**
 *
 * @author Wezzy Laptop
 */
public class MovieDAOTester {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        MovieDAO m = new MovieDAO();
        m.scanFolder("C:\\Users\\Wezzy Laptop\\Desktop\\Musik2");
    }
    
}
