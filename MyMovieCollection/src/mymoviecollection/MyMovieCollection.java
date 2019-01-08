/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mymoviecollection.dal.DatabaseConnection;

/**
 *
 * @author Wezzy Laptop
 */
public class MyMovieCollection extends Application {

    @Override
    public void start(Stage stage) throws IOException, SQLServerException {
//        Parent root = FXMLLoader.load(getClass().getResource("/mymovieCollection/gui/view/MyMoviesMainView.fxml"));
//        Scene scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();        
        DatabaseConnection db = new DatabaseConnection();
        db.getConnection();
        System.out.println("Virker");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
