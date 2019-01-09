/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.gui.controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import mymoviecollection.be.Category;
import mymoviecollection.be.Movie;
import mymoviecollection.bll.Manager;
import mymoviecollection.bll.Player;
import mymoviecollection.bll.Search;

/**
 *
 * @author Wezzy Laptop
 */
public class Model
{

    private Player player;
    private Search search;
    private ObservableList<Movie> movies;
    private ObservableList<Category> categories;
    private Manager manager;

    public Model() throws IOException
    {
        player = new Player();
        search = new Search();
        movies = FXCollections.observableArrayList();
        categories = FXCollections.observableArrayList();
        manager = new Manager();
    }

    public void addMovies(Stage stage)
    {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null)
        {
            String filepath = selectedDirectory.getAbsolutePath();
            manager.scanFolder(filepath);
        }

    }

    void editMovie(Movie selectedItem)
    {
        manager.editMovie(selectedItem);
    }
    
    void editCat()
    {
        TextField txtTitle = new TextField();
        txtTitle.setText("new category name");
        Button btn = new Button();
        btn.setText("edit category");
        ComboBox cbox = new ComboBox<Category>();
        cbox.setItems(manager.getAllCategories());
        StackPane root = new StackPane();
        root.setAlignment(txtTitle, Pos.TOP_CENTER);
        root.setAlignment(btn, Pos.BOTTOM_CENTER);
        root.getChildren().addAll(txtTitle, btn, cbox);
        Scene scene = new Scene(root, 200, 80);
        Stage stage = new Stage();
        stage.setTitle("edit category");
        stage.setScene(scene);
        stage.show();
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try
                {
                    editCategory((Category) cbox.getSelectionModel().getSelectedItem(), txtTitle.getText());
                } catch (SQLException ex)
                {
                    Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
                }
                Stage stage = (Stage) txtTitle.getScene().getWindow();
                stage.close();
            }
        });
    }
    void editCategory(Category category, String newTitle) throws SQLException {
        manager.editCategory(category, newTitle);

    }

    void playMovie(Movie selectedItem)
    {
        manager.playMovie(selectedItem);
    }

    public void sliderRateMovie(Movie selectedItem, double value)
    {
        if (selectedItem !=null)
        {
            manager.sliderRateMovie(selectedItem, value);
        }
    }

    public double getLabelRating(double value)
    {
        
    return value;
    }
    
    void reMovie(Movie selectedItem)
    {
        manager.reMovie(selectedItem);
    }

    void createCategory()
    {
        TextField txtTitle = new TextField();
        txtTitle.setText("Category");
        Button btn = new Button();
        btn.setText("Create category");
        StackPane root = new StackPane();
        root.setAlignment(txtTitle, Pos.TOP_CENTER);
        root.setAlignment(btn, Pos.BOTTOM_CENTER);
        root.getChildren().addAll(txtTitle, btn);
        Scene scene = new Scene(root, 200, 50);
        Stage stage = new Stage();
        stage.setTitle("create category");
        stage.setScene(scene);
        stage.show();
        btn.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                addCategory(new Category(txtTitle.getText()));
                Stage stage = (Stage) txtTitle.getScene().getWindow();
                stage.close();
            }
        });
    }

    void addCategory(Category cat)
    {
        //categories.add(new Category(cat.getTitle()));
        manager.addCategory(cat);
    }

    void chooseDeleteCategory()
    {
//        TextField txtTitle = new TextField();
//        txtTitle.setText("Category");
        Button btn = new Button();
        ComboBox cbox = new ComboBox<Category>();
        cbox.setItems(manager.getAllCategories());
        
        
        btn.setText("delete category");
        StackPane root = new StackPane();
        root.setAlignment(cbox, Pos.TOP_CENTER);
        root.setAlignment(btn, Pos.BOTTOM_CENTER);
        root.getChildren().addAll(cbox, btn);
        Scene scene = new Scene(root, 200, 50);
        Stage stage = new Stage();
        stage.setTitle("delete category");
        stage.setScene(scene);
        stage.show();
        btn.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override

            public void handle(ActionEvent event) {
                try
                {
                    deleteCategory((Category) cbox.getSelectionModel().getSelectedItem());
                } catch (SQLException ex)
                {
                    Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
                }
                Stage stage = (Stage) btn.getScene().getWindow();

                stage.close();
            }
        });
    }

    void deleteCategory(Category category) throws SQLException {
        manager.deleteCategory(category);

    }

    public ObservableList<Movie> getAllMovies()
    {
        return manager.getAllMovies();
    }


    ObservableList<Category> getAllCategories()
    {
        return manager.getAllCategories();
    }

    void searchMovie(String query) {
        search.searchMovie(query);
    }
    
    
    

}
