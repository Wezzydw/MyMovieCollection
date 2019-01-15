/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mymoviecollection.be.Category;
import mymoviecollection.be.Movie;

/**
 * FXML Controller class
 *
 * @author andreas
 */
public class EditMovieViewController implements Initializable
{

    @FXML
    private TextField txtTitle;
    @FXML
    private TextField txtLength;
    @FXML
    private TextField txtReleaseYear;
    @FXML
    private TextField txtRating;
    @FXML
    private MenuButton menuCategories;

    Model model;
    private Movie selectedMovie;
    private String d2;
    private int Id;
    private int movieIndex;
    private List<Boolean> selectedCategories = new ArrayList();
    @FXML
    private Button btnSave;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
        
        try {
            model = new Model();
        } catch (IOException ex) {
            Logger.getLogger(EditMovieViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        EventHandler<ActionEvent> event1 = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            { 
                if (((CheckMenuItem)e.getSource()).isSelected())
                {
                    Category mygod = new Category("hi");
                    int tmpIndex = menuCategories.getItems().indexOf(((CheckMenuItem)e.getSource()));
                    selectedCategories.remove(tmpIndex);
                    selectedCategories.add(tmpIndex, Boolean.TRUE);
                    model.sortCategories();
                }
                else
                {
                    int tmpIndex = menuCategories.getItems().indexOf(((CheckMenuItem)e.getSource()));
                    selectedCategories.remove(tmpIndex);
                    selectedCategories.add(tmpIndex, Boolean.FALSE);
                    model.sortCategories();
                }
            } 
        }; 
        
        
        
    }   
    
    public void setMovie(Movie movie)
    {
        selectedMovie = movie;
        txtTitle.setText(selectedMovie.getTitle());
        txtLength.setText(selectedMovie.getLength());
        txtRating.setText("" + selectedMovie.getRating());
        txtReleaseYear.setText(selectedMovie.getReleaseYear());
        this.Id = selectedMovie.getId();
        this.movieIndex = movieIndex;
        
        List<Category> allCategories = model.getAllCategories();
        for (Category allCategory : allCategories)
        {
            selectedCategories.add(Boolean.FALSE);
        }
        String s = "";
        for (String string : selectedMovie.getCategory())
        {
            s += string + ",";
        }
//        EventHandler<ActionEvent> event1 = new EventHandler<ActionEvent>() { 
//            public void handle(ActionEvent e) 
//            { 
//                if (((CheckMenuItem)e.getSource()).isSelected())
//                {
//                    int tmpIndex = menuCategories.getItems().indexOf(((CheckMenuItem)e.getSource()));
//                    selectedCategories.set(tmpIndex, Boolean.TRUE);
//                }
//                else
//                {
//                    int tmpIndex = menuCategories.getItems().indexOf(((CheckMenuItem)e.getSource()));
//                    selectedCategories.set(tmpIndex, Boolean.FALSE);
//                }
//            } 
//        }; 

        List<CheckMenuItem> allItems = new ArrayList();
        for (Category allCategory : allCategories)
        {
            CheckMenuItem iti = new CheckMenuItem(allCategory.getTitle());
            allItems.add(iti);
        }
        model.getAllCategories();
        menuCategories.getItems().addAll(allItems);
        String[] splitted = s.split(",");
        for (String string : splitted)
        {
            int ocu = 0;
            for (Category allCategory : allCategories)
            {
                if (allCategory.toString().equals(string))
                {
                    System.out.println("1111111111111111111111111111111111111111111111111111111111");
                    selectedCategories.set(ocu, Boolean.TRUE);
                    CheckMenuItem eel = new CheckMenuItem(allCategories.get(ocu).toString());
                    menuCategories.getItems().set(ocu, eel);
                    
//                    menuCategories.getItems().get(ocu).set;
//                    menuCategories.getItems().get(ocu).;
//                    menuCategories.getItems().se
//                    selectedCategories.remove(ocu);
//                    selectedCategories.add(ocu, Boolean.TRUE);
                }
                ocu++;
            }
        }
//        this.d2 = selectedSong.getLength();
//        this.songIndex = songIndex;
    }
    
    public void setModel(Model model)
    {
        this.model = model;
    }

    @FXML
    private void onSave(ActionEvent event) throws IOException
    {
        String title = txtTitle.getText();
        String releaseYear = txtReleaseYear.getText();
        String length = txtLength.getText();
        String rating = txtRating.getText();
        List<String> categories = new ArrayList();
        for (int i = 0; i < selectedCategories.size(); i++)
        {
            if (selectedCategories.get(i))
            {
                categories.add(model.getAllCategories().get(i).toString());
            }
        }
        
//        String categori = comboCategory.getSelectionModel().getSelectedItem();
        
        int id = Id;
        Movie m = new Movie(title, length, releaseYear, categories, selectedMovie.getFilePath(), selectedMovie.getPosterPath(), selectedMovie.getImdbRating());
        model.updateMovie(m);
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/mytunes/gui/view/MyTunesMainView.fxml"));
        
        try
        {
            loader.load();
        } catch (IOException ex)
        {
            Logger.getLogger(EditMovieViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        MyMoviesMainViewController display = loader.getController();
        display.model.updateMovie(m);

        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }
}
