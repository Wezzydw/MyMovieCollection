/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mymoviecollection.be.Movie;
import mymoviecollection.bll.Player;
import mymoviecollection.bll.Search;

/**
 *
 * @author Wezzy Laptop
 */
public class Model {
    
    private Player player;
    private Search search;
    private ObservableList<Movie> movies;
    
    public Model(){
        player = new Player();
        search = new Search();
        movies = FXCollections.observableArrayList();
    }
    
}
