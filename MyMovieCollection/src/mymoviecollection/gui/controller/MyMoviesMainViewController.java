/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.gui.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mymoviecollection.be.Category;
import mymoviecollection.be.Movie;
import mymoviecollection.bll.BLLException;
import mymoviecollection.dal.DALException;

/**
 * FXML Controller class
 *
 * @author andreas
 */
public class MyMoviesMainViewController implements Initializable
{

    @FXML
    private ListView<Movie> lstMov;
    @FXML
    private TextField txtSearch;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblInfo = new Label("\t\t\t\t" + "no menu item selected");
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private MenuButton menuCategory;
    @FXML
    private Slider sliderRateMovie;
    @FXML
    private Label movieRating;
    @FXML
    private Label lblYear;
    @FXML
    private Label lblLength;
    @FXML
    private Label lblImdb;
    @FXML
    private Label lblCategories;
    @FXML
    private ImageView MovieImage;

    private List<Boolean> selectedCategories;
    Model model;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        try
        {
            model = new Model();
        } catch (IOException ex)
        {
            System.out.println("Inititalize model error : " + ex.getMessage());
        }

        model.initCategories(menuCategory);
        lstMov.setItems(model.getAllMovies());
        model.setMenuItmes(menuCategory, model.getAllCategories());
        model.setCheckList(selectedCategories);
        model.movieReminder();
    }

    @FXML
    private void btnRemoveCat(ActionEvent event)
    {
        model.chooseDeleteCategory();
        menuCategory.getItems().clear();
        menuCategory.getItems().addAll(model.getMenuItems());
    }

    @FXML
    private void btnAddCat(ActionEvent event)
    {
        model.createCategory();
    }

    @FXML
    private void btnRemoveMov(ActionEvent event) throws IOException
    {
        model.reMovie(lstMov.getSelectionModel().getSelectedItems());
    }

    @FXML
    private void btnAddMov(ActionEvent event)
    {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        model.addMovies(stage);
    }

    @FXML
    private void btnEditMov(ActionEvent event) throws IOException
    {
        Movie selectedMovie = lstMov.getSelectionModel().getSelectedItem();
        
        model.editMovie(selectedMovie);
    }

    @FXML
    private void btnRenameCat(ActionEvent event)
    {
        model.editCat();
    }

    @FXML
    private void btnPlayMov(ActionEvent event)
    {
        Movie selectedMovie = lstMov.getSelectionModel().getSelectedItem();
        if (selectedMovie != null)
        {
            LocalDate date1 = LocalDate.now();
            lblInfo.setText(date1.toString());
            selectedMovie.setLastView(date1.toString());
            model.playMovie(selectedMovie);
        }
    }

    @FXML
    private void txtSearch(KeyEvent event)
    {
        model.searchMovie(txtSearch.getText());
    }

    @FXML
    private void OnHandleMovieRated(MouseEvent event)
    {
        double sliderVal = sliderRateMovie.getValue();
        Movie selectedMovie = lstMov.getSelectionModel().getSelectedItem();
        
        movieRating.setText("" + model.getLabelRating(sliderVal));
        model.sliderRateMovie(selectedMovie, sliderVal);
        
        Stage stage = (Stage) anchorPane.getScene().getWindow();
    }


    @FXML
    private void selectedDataToManager(MouseEvent event)
    {
        Movie selectedMovie = lstMov.getSelectionModel().getSelectedItem();
        if (selectedMovie != null)
        {
            lblInfo.setText("" + selectedMovie.getLastView());
            lblTitle.setText(selectedMovie.getTitle());
            lblLength.setText("Movie length " + selectedMovie.getLength() + " Minutes");
            lblYear.setText("Release year " + selectedMovie.getReleaseYear());
            
            if (model.getImage(selectedMovie.getPosterPath()) != null)
            {
                Image image = SwingFXUtils.toFXImage(model.getImage(selectedMovie.getPosterPath()), null);
                MovieImage.setImage(image);
            }

            lblCategories.setText(model.getCategoryString(selectedMovie));

            lblImdb.setText("" + selectedMovie.getImdbRating());
            lblInfo.setText("" + selectedMovie.getLastView());

            if (sliderRateMovie != null)
            {
                sliderRateMovie.setValue(selectedMovie.getRating());
                movieRating.setText("" + selectedMovie.getRating());
            } else
            {
                sliderRateMovie.setValue(0);
                movieRating.setText("");
            }
        }
    }
}
