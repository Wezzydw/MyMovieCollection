/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.gui.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import mymoviecollection.be.Category;
import mymoviecollection.be.Movie;
import mymoviecollection.bll.Manager;
import mymoviecollection.dal.DALException;

/**
 *
 * @author Wezzy Laptop
 */
public class Model
{
    private static final String editMoviefxml = "/mymoviecollection/gui/view/EditMovieView.fxml";
    private List<Movie> moviesReminder;
    private Manager manager;
    private String tmpString;
    private MenuButton mmm;
    private List<Boolean> categoryCheck;
    private List<Boolean> selectedCategories;

    public Model() throws IOException
    {
        try
        {
            manager = new Manager();
        } catch (SQLException ex)
        {
            System.out.println("Error making new Manager " + ex);;
        }
        tmpString = "";
    }

    /**
     * metoden laver en list af booleans og sætter variablen til dens input.
     *
     * @param categoryCheck
     */
    public void setCheckList(List<Boolean> categoryCheck)
    {
        this.categoryCheck = categoryCheck;
    }

    /**
     * sender metoden videre til manager.
     *
     * @param mmm
     * @param allCat
     */
    public void setMenuItmes(MenuButton mmm, List<Category> allCat)
    {
        this.mmm = mmm;
        manager.getChecklistCategories(allCat);
    }

    /**
     * hvis selectedDirectory ikke er null så sender den videre til manager.
     *
     * @param stage
     * @throws DALException
     */
    public void addMovies(Stage stage)
    {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null)
        {
            String filepath = selectedDirectory.getAbsolutePath();
            try
            {
                manager.scanFolder(filepath);
            } catch (DALException ex)
            {
                System.out.println("Error in scanning folder" + ex);
            }
        }
    }

    /**
     * Denne metode åbner et FXML vindue hvori det er muligt at editmovies.
     *
     * @param selectedItem
     * @throws IOException
     */
    void editMovie(Movie selectedItem)
    {
        if (selectedItem != null)
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(editMoviefxml));
            try
            {
                loader.load();
            } catch (IOException ex)
            {
                System.out.println("Error editing the selected movie" + ex);
            }
            EditMovieViewController display = loader.getController();

            display.setModel(this);
            display.setMovie(selectedItem);
            Parent p = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(p));
            stage.showAndWait();
        }
        /**
         * metoden opdatere den film der er bleven ændret i editMovie.
         */

    }

    void updateMovie(Movie selectedItem)
    {
        manager.editMovie(selectedItem);
    }

    /**
     * denne metode åbner et vindue der gør det muligt at ændre kategorierne på
     * den valgte kategori.
     */
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
        btn.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                editCategory((Category) cbox.getSelectionModel().getSelectedItem(), txtTitle.getText());
                mmm.getItems().remove(cbox.getItems().indexOf(cbox.getSelectionModel().getSelectedItem()) + 1);
                mmm.getItems().add(new CheckMenuItem(txtTitle.getText()));
                Stage stage = (Stage) txtTitle.getScene().getWindow();
                stage.close();
            }
        });
    }

    /**
     * Sender metoden ned til manageren, med category og en ny title.
     *
     * @param category
     * @param newTitle
     * @throws SQLException
     */
    void editCategory(Category category, String newTitle)
    {
        try
        {
            manager.editCategory(category, newTitle);
        } catch (SQLException ex)
        {
            System.out.println("Error in editing selected category" + ex);
        }

    }

    /**
     * Sender den selected movie til manager.
     *
     * @param selectedItem
     */
    void playMovie(Movie selectedItem)
    {
        try
        {
            manager.playMovie(selectedItem);
        } catch (IOException ex)
        {
            System.out.println("Error in playing movie" + ex);
        }
    }

    /**
     * Sender et selected item og value(rating) til manager
     *
     * @param selectedItem
     * @param value
     */
    public void sliderRateMovie(Movie selectedItem, double value)
    {
        if (selectedItem != null)
        {
            try
            {
                manager.sliderRateMovie(selectedItem, value);
            } catch (IOException ex)
            {
                System.out.println("Error rating movie" + ex);
            }
        }
    }

    /**
     * @param value
     * @return returnere en value som rating
     */
    public double getLabelRating(double value)
    {
        return value;
    }

    /**
     * sender selected item til manager laget.
     *
     * @param selectedItem
     * @throws IOException
     */
    void reMovie(List<Movie> selectedItem)
    {
        try
        {
            manager.reMovie(selectedItem);
        } catch (IOException ex)
        {
            System.out.println("Error reMovie-ing selected movie" + ex);
        }
    }

    /**
     * åbner et vindue hvori man kan create en ny category.
     */
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
                tmpString = txtTitle.getText();
                mmm.getItems().add(new CheckMenuItem(tmpString));
                Stage stage = (Stage) txtTitle.getScene().getWindow();
                stage.close();
            }
        });
    }

    /**
     * kalder metoden i manager
     *
     * @param cat
     */
    void addCategory(Category cat)
    {
        manager.addCategory(cat);
    }

    /**
     * metoden åbner et advarselsvindue hvis der er film i moviereminder listen.
     * Så kalder den metoden i manager.warning.
     */
    void movieReminder()
    {
        Button btn = new Button("OK");
        Label lbl = new Label();
        moviesReminder = new ArrayList();
        moviesReminder.addAll(manager.warning());
        String underWatchedMovie = "";
        if (moviesReminder.size() != 0)
        {
            for (Movie movy : moviesReminder)
            {
                underWatchedMovie = underWatchedMovie + movy.getTitle() + "\n";
            }

            lbl.setText(underWatchedMovie);

            StackPane root = new StackPane();
            root.setAlignment(lbl, Pos.TOP_CENTER);
            root.setAlignment(btn, Pos.BOTTOM_CENTER);
            root.getChildren().addAll(lbl, btn);
            Scene scene = new Scene(root, 800, 600);
            Stage stage = new Stage();
            stage.setTitle("Consider removing these movies");
            stage.setScene(scene);
            stage.show();
            btn.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override

                public void handle(ActionEvent event)
                {
                    Stage stage = (Stage) btn.getScene().getWindow();
                    stage.close();
                }
            });
        }
    }

    /**
     * Laver et vindue hvori man kan delete valgte kategorier.
     */
    void chooseDeleteCategory()
    {
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

            /**
             * den åbner et vindue med en combobox hvori man kan vælge de
             * kategorier der skal slettes
             */
            public void handle(ActionEvent event)
            {
                deleteCategory((Category) cbox.getSelectionModel().getSelectedItem());
                mmm.getItems().remove(cbox.getItems().indexOf(cbox.getSelectionModel().getSelectedItem()) + 1);
                Stage stage = (Stage) btn.getScene().getWindow();

                stage.close();
            }
        });
    }

    /**
     * sender metoden til manager.
     *
     * @param category
     * @throws SQLException
     */
    void deleteCategory(Category category)
    {
        try
        {
            manager.deleteCategory(category);
        } catch (SQLException ex)
        {
            System.out.println("Error in deleting movie" + ex);
        }
    }

    /**
     *
     * @return kalder metoden getAllMovies i manager
     */
    public ObservableList<Movie> getAllMovies()
    {
        return manager.getAllMovies();
    }

    /**
     * kalder cateogry listen ned til manager.
     *
     * @return
     */
    ObservableList<Category> getAllCategories()
    {
        return manager.getAllCategories();
    }

    /**
     * kalder SearchMovie i manager
     *
     * @param query
     */
    void searchMovie(String query)
    {
        manager.searchMovie(query);
    }

    /**
     * kalder SortCategories i manager
     */
    void sortCategories()
    {
        manager.sortCategories(categoryCheck);
    }

    /**
     * hvis billedet ikke er tomt kalder den getImage i manager.
     *
     * @param image
     * @return image
     */
    public BufferedImage getImage(String image)
    {
        if (!image.isEmpty())
        {
            try
            {
                return manager.getImage(image);
            } catch (DALException ex)
            {
                System.out.println(ex.getMessage());
            }
        }
        return null;
    }

    List<CheckMenuItem> getMenuItems()
    {
        List<Category> allCategories = manager.getAllCategories();
        List<CheckMenuItem> allItems = new ArrayList();
        for (Category allCategory : allCategories)
        {
            CheckMenuItem iti = new CheckMenuItem(allCategory.getTitle());
            allItems.add(iti);
        }
        return allItems;
    }

    public void initCategories(MenuButton menuCategory)
    {
        selectedCategories = new ArrayList();
        List<Category> allCategories = new ArrayList();
        allCategories.addAll(getAllCategories());
        int counter = 0;
        for (int i = 0; i < allCategories.size(); i++) //+1 er for all movies, så måske skal det ændres
        {
            selectedCategories.add(Boolean.FALSE);
        }

        EventHandler<ActionEvent> event1 = new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                if (((CheckMenuItem) e.getSource()).isSelected())
                {
                    int tmpIndex = menuCategory.getItems().indexOf(((CheckMenuItem) e.getSource()));
                    selectedCategories.set(tmpIndex, Boolean.TRUE);
                    sortCategories();
                } else
                {
                    int tmpIndex = menuCategory.getItems().indexOf(((CheckMenuItem) e.getSource()));

                    selectedCategories.set(tmpIndex, Boolean.FALSE);
                    sortCategories();
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
        menuCategory.getItems().addAll(allItems);
    }

    public String getCategoryString(Movie m)
    {
        String string = "";
        int counter = 0;
        
        for (String category : m.getCategory())
        {
            counter++;
            if (counter == m.getCategory().size())
            {
                tmpString = tmpString + category;
            } else
            {
                tmpString = tmpString + category + ", ";
            }
        }
        return string;
    }

}
