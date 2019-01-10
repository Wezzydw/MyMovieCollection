/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
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
    private Label lblInfo;
    @FXML
    private ChoiceBox<Category> choiceBoxCat;
    
    private Model model;
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
        int counter = 0;
        choiceBoxCat.setItems(model.getAllCategories());
        
        List<Category> allCategories = model.getAllCategories();
        List<CheckMenuItem> allItems = new ArrayList();
        for (Category allCategory : allCategories)
        {
            counter++;
            String name = "item" + counter;
            CheckMenuItem iti = new CheckMenuItem(allCategory.getTitle());
            allItems.add(iti);
        }
//        menuCategory.getItems().remove(rb);
        menuCategory.getItems().addAll(allItems);
        lstMov.setItems(model.getAllMovies());


    }    

    @FXML
    private void btnRemoveCat(ActionEvent event)
    {
//        model.deleteCategory();
        model.chooseDeleteCategory();
    }

    @FXML
    private void btnAddCat(ActionEvent event)
    {
        model.createCategory();
//        List<Category> allCategories = model.getAllCategories();
//        String lastAdded = allCategories.get(allCategories.size()).getTitle();
//        menuCategory.getItems().add(new CheckMenuItem(lastAdded));
    }

    @FXML
    private void btnRemoveMov(ActionEvent event)
    {
        model.reMovie(lstMov.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void btnAddMov(ActionEvent event)
    {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        model.addMovies(stage);
        
    }

    @FXML
    private void btnEditMov(ActionEvent event)
    {
        model.editMovie(lstMov.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void btnRenameCat(ActionEvent event)
    {
        model.editCat();
    }

    @FXML
    private void btnRate(ActionEvent event)
    {
        //model.rateMovie(lstMov.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void btnPlayMov(ActionEvent event)
    {
        model.playMovie(lstMov.getSelectionModel().getSelectedItem());
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
        model.onProgramClose(stage);        
        System.out.println("VIrker det");
    }

    @FXML
    private void handleSelectMenuItem(ActionEvent event)
    {
//        int[] selectedItems = menuCategory.;
        
        
//        int selectedIndex = comboBox.getSelectionModel().getSelectedIndex();
//        String txtValue;
//        String resultText = "";
//        int numberOfLetters = 0;
//        switch (selectedIndex)
//        {
//            case 0:
//                txtValue = txtInput.getText();
//                //txtValue = txtValue.toUpperCase();
//                txtValue = tsmodel.test(txtValue);
//                break;
//            case 1:
//                txtValue = txtInput.getText();
//                txtValue = tsmodel.getFirstName(txtValue);
//                break;
//            case 2:
//                txtValue = txtInput.getText();
//                txtValue = tsmodel.getLastName(txtValue);
//                break;
//            case 3:
//                txtValue = txtInput.getText();
//                numberOfLetters = tsmodel.getNumberOfLetters(txtValue);
//                txtValue = String.valueOf(numberOfLetters);
//                break;
//            case 4:
//                txtValue = txtInput.getText();
//                txtValue = tsmodel.getFormattedName(txtValue);
//                break;
//            default:
//                throw new UnsupportedOperationException("Selected conversion not supported yet");
//        }
//        
//        resultText = String.valueOf(txtValue);
//        lblResult.setText(resultText);
    }
    
}
