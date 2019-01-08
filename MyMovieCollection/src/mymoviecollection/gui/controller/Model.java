/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.gui.controller;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import mymoviecollection.be.Movie;
import mymoviecollection.bll.Manager;
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
    private Manager manager;
    
    public Model(){
        player = new Player();
        search = new Search();
        movies = FXCollections.observableArrayList();
        manager = new Manager();
    }
    
    public void addMovies(Stage stage){
        
         DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            String filepath = selectedDirectory.getAbsolutePath();
            manager.scanFolder(filepath);
        }
        
    }

    void editMovie(Movie selectedItem) {
        manager.editMovie(selectedItem);
    }

    void editCategory() {
        manager.editCategory();
    }

    void playMovie(Movie selectedItem) {
        manager.playMovie(selectedItem);
    }

    void rateMovie(Movie selectedItem) {
        manager.rateMovie(selectedItem);
    }

    void reMovie(Movie selectedItem) {
        manager.reMovie(selectedItem);
    }

    void addCategory() {
        manager.addCategory();
    }

    void deleteCategory() {
        manager.deleteCategory();
    }

    public ObservableList<Movie> getAllMovies() {
        return manager.getAllMovies();
        
    }
    
    
    
}
