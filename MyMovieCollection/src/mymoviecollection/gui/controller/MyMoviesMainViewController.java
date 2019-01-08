/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author andreas
 */
public class MyMoviesMainViewController implements Initializable
{

    @FXML
    private ListView<?> lstMov;
    @FXML
    private TextField txtSearch;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblInfo;
    @FXML
    private ChoiceBox<?> choiceBoxCat;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
    }    

    @FXML
    private void btnRemoveCat(ActionEvent event)
    {
    }

    @FXML
    private void btnAddCat(ActionEvent event)
    {
    }

    @FXML
    private void btnRemoveMov(ActionEvent event)
    {
    }

    @FXML
    private void btnAddMov(ActionEvent event)
    {
    }

    @FXML
    private void btnEditMov(ActionEvent event)
    {
    }

    @FXML
    private void btnRenameCat(ActionEvent event)
    {
    }

    @FXML
    private void btnRate(ActionEvent event)
    {
    }

    @FXML
    private void btnPlayMov(ActionEvent event)
    {
    }
    
}
