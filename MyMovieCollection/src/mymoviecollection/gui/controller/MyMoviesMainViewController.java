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
    private ChoiceBox<Category> choiceBoxCat;

    Model model;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private MenuButton menuCategory;

    @FXML
    private Slider sliderRateMovie;
    @FXML
    private Label movieRating;
    @FXML
    private ImageView StarImage;

    private List<Boolean> selectedCategories;
    @FXML
    private Label lblYear;
    @FXML
    private Label lblLength;
    @FXML
    private Label lblImdb;
    @FXML
    private Label lblCategories;
    @FXML
    private ImageView ImdbRating;
    @FXML
    private ImageView MovieImage;

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
            Logger.getLogger(MyMoviesMainViewController.class.getName()).log(Level.SEVERE, null, ex);
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
        try
        {
            model.addMovies(stage);
        } catch (DALException ex)
        {
            Logger.getLogger(MyMoviesMainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //DO SOMETHING WITH ERROR HERE

    }

    @FXML
    private void btnEditMov(ActionEvent event) throws IOException
    {
        model.editMovie(lstMov.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void btnRenameCat(ActionEvent event)
    {
        model.editCat();
    }

    private void btnRate(ActionEvent event)
    {
        //model.rateMovie(lstMov.getSelectionModel().getSelectedItem());

    }

    @FXML
    private void btnPlayMov(ActionEvent event)
    {
        if (lstMov.getSelectionModel().getSelectedItem() != null)
        {
            LocalDate date1 = LocalDate.now();
            lblInfo.setText(date1.toString());
            Movie m = (lstMov.getSelectionModel().getSelectedItem());
            m.setLastView(date1.toString());
            model.playMovie(m);
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
        movieRating.setText("" + model.getLabelRating(sliderRateMovie.getValue()));
        model.sliderRateMovie(lstMov.getSelectionModel().getSelectedItem(), sliderRateMovie.getValue());
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        System.out.println("VIrker det");
    }

    @FXML
    private void handleSelectMenuItem(ActionEvent event)
    {
        System.out.println("menu handle");
    }

    @FXML
    private void selectedDataToManager(MouseEvent event)
    {
        if (lstMov.getSelectionModel().getSelectedItem() != null)
        {
            System.out.println("LastView : " + lstMov.getSelectionModel().getSelectedItem().getLastView());
            lblInfo.setText(" " + lstMov.getSelectionModel().getSelectedItem().getLastView());
            model.sendDataOnClick(lstMov.getSelectionModel().getSelectedItems());
            lblTitle.setText(lstMov.getSelectionModel().getSelectedItem().getTitle());
            lblLength.setText(" Movie length " + lstMov.getSelectionModel().getSelectedItem().getLength() + " Minutes");
            lblYear.setText(" Release year " + lstMov.getSelectionModel().getSelectedItem().getReleaseYear());
            System.out.println("Init poster path; " + lstMov.getSelectionModel().getSelectedItem().getPosterPath());
            //MovieImage = new ImageView(model.getImage(lstMov.getSelectionModel().getSelectedItem().getPosterPath())); 
            System.out.println("Image in controller: " + lstMov.getSelectionModel().getSelectedItem().getPosterPath());
            if (model.getImage(lstMov.getSelectionModel().getSelectedItem().getPosterPath()) != null)
            {
                Image image = SwingFXUtils.toFXImage(model.getImage(lstMov.getSelectionModel().getSelectedItem().getPosterPath()), null);
                MovieImage.setImage(image);
            }

            String tmpString = "";

            int counter = 0;
            System.out.println(lstMov.getSelectionModel().getSelectedItem().getCategory());
            for (String category : lstMov.getSelectionModel().getSelectedItem().getCategory())
            {
                counter++;

                if (counter == lstMov.getSelectionModel().getSelectedItem().getCategory().size())
                {
                    tmpString = tmpString + category;
                } else
                {
                    tmpString = tmpString + category + ", ";
                }
            }
            lblCategories.setText(tmpString);

            lblImdb.setText("" + lstMov.getSelectionModel().getSelectedItem().getImdbRating());
            lblInfo.setText("" + lstMov.getSelectionModel().getSelectedItem().getLastView());

            if (sliderRateMovie != null)
            {
                sliderRateMovie.setValue(lstMov.getSelectionModel().getSelectedItem().getRating());
                movieRating.setText("" + lstMov.getSelectionModel().getSelectedItem().getRating());
            } else
            {
                sliderRateMovie.setValue(0);
                movieRating.setText("");
            }
        }
    }
}
