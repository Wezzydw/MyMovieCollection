/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.gui.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
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
        selectedCategories = new ArrayList();
        
        try
        {
            model = new Model();
        } catch (IOException ex)
        {
            Logger.getLogger(MyMoviesMainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Category> allCategories = new ArrayList();
        allCategories.addAll(model.getAllCategories());
        int counter = 0;
        for (int i = 0; i < allCategories.size(); i++) //+1 er for all movies, så måske skal det ændres
        {
            selectedCategories.add(Boolean.FALSE);            
        }
        choiceBoxCat.setItems(model.getAllCategories());
        EventHandler<ActionEvent> event1 = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            { 
                if (((CheckMenuItem)e.getSource()).isSelected())
                {
                    Category mygod = new Category("hi");
                    int tmpIndex = menuCategory.getItems().indexOf(((CheckMenuItem)e.getSource()));
                    selectedCategories.remove(tmpIndex);
                    selectedCategories.add(tmpIndex, Boolean.TRUE);
                    model.sortCategories();
                }
                else
                {
                    int tmpIndex = menuCategory.getItems().indexOf(((CheckMenuItem)e.getSource()));
                    selectedCategories.remove(tmpIndex);
                    selectedCategories.add(tmpIndex, Boolean.FALSE);
                    model.sortCategories();
                }
            } 
        }; 
        List<CheckMenuItem> allItems = new ArrayList();
        for (Category allCategory : allCategories)
        {
            counter++;
            String name = "item" + counter;
            CheckMenuItem iti = new CheckMenuItem(allCategory.getTitle());
            iti.setOnAction(event1);
            allItems.add(iti);
        }
//        menuCategory.getItems().remove(rb);
        menuCategory.getItems().addAll(allItems);
        lstMov.setItems(model.getAllMovies());
        
        model.setMenuItmes(menuCategory, allCategories);
        model.setCheckList(selectedCategories);
    }    

    @FXML
    private void btnRemoveCat(ActionEvent event)
    {
//        model.deleteCategory();
        model.chooseDeleteCategory();
        menuCategory.getItems().clear();
        List<Category> allCategories = model.getAllCategories();
        List<CheckMenuItem> allItems = new ArrayList();
        for (Category allCategory : allCategories)
        {
            CheckMenuItem iti = new CheckMenuItem(allCategory.getTitle());
            allItems.add(iti);
        }
        model.getAllCategories();
        menuCategory.getItems().addAll(allItems);
    }

    @FXML
    private void btnAddCat(ActionEvent event)
    {
        model.createCategory();
//        List<Category> allCategories = model.getAllCategories();
//        String lastAdded = allCategories.get(allCategories.size()).getTitle();
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
    private void btnEditMov(ActionEvent event)
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
        model.deleteHalf();
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
        System.out.println("menu handle");
    }

    @FXML
    private void selectedDataToManager(MouseEvent event)
    {
        BufferedImage buff = null;
       model.sendDataOnClick(lstMov.getSelectionModel().getSelectedItems());
       lblTitle.setText(lstMov.getSelectionModel().getSelectedItem().getTitle());
       lblLength.setText(" Movie length "+ lstMov.getSelectionModel().getSelectedItem().getLength()+" Minutes");
       lblYear.setText(" Release year "+ lstMov.getSelectionModel().getSelectedItem().getReleaseYear());
        System.out.println("Init poster path; " + lstMov.getSelectionModel().getSelectedItem().getPosterPath());
       //MovieImage = new ImageView(model.getImage(lstMov.getSelectionModel().getSelectedItem().getPosterPath())); 
       Image image = SwingFXUtils.toFXImage(model.getImage(lstMov.getSelectionModel().getSelectedItem().getPosterPath()), null);
       MovieImage.setImage(image);
       String tmpString = "";
       
       int counter = 0;
       
       
       for(String category : lstMov.getSelectionModel().getSelectedItem().getCategory())
       {
           counter ++;
           if(counter ==  lstMov.getSelectionModel().getSelectedItem().getCategory().size()) 
           {
           tmpString = tmpString + category;
           }
           else 
           {
             tmpString = tmpString + category + ", ";
           }
       }
       lblCategories.setText(tmpString);
       
       lblImdb.setText(""+ lstMov.getSelectionModel().getSelectedItem().getImdbRating());
       lblInfo.setText(""+ lstMov.getSelectionModel().getSelectedItem().getLastView());
    }

}
    
