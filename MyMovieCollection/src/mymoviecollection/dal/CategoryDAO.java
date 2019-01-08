/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.dal;



import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mymoviecollection.be.Category;

/**
 *
 * @author Andreas Svendsen
 */
public class CategoryDAO {
    
    DatabaseConnection conProvider;

    public CategoryDAO() throws IOException
    {
        this.conProvider = new DatabaseConnection();
    }
    //Vi skal have begrænsninger på ikke at lave en Category som allerede findes,
    // men det skal nok laves i bll laget
    public void createCategory(String title)
    {
        try (Connection con = conProvider.getConnection())
        {
            try (PreparedStatement pstmt = con.prepareStatement("INSERT INTO Category (name) VALUES (?)"))
            {
                pstmt.setString(1, title);
                pstmt.execute();
                pstmt.close();
            }
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }
    // skal måske også slette i CatMov, da det er sammenhænget mellem category og movie
    public void deleteCategory(String title) throws SQLException //String skal måske være category fra BE laget
    {
        try (Connection con = conProvider.getConnection())
        {
            String a = "DELETE FROM Category WHERE name = (?);";
            PreparedStatement pstmt = con.prepareStatement(a);
            pstmt.setString(1, title);
            pstmt.execute();
            pstmt.close();
            //ikke testet
            a = "Select * From catMov;";
            ResultSet rs = pstmt.executeQuery(a);
            while (rs.next())
            {
                a = "DELETE FROM Playlist WHERE Title = (?);";
                pstmt = con.prepareStatement(a);
                pstmt.setString(1, title);
                pstmt.execute();
            }
            //hertil
        } catch (SQLServerException ex)
        {
        }
    }
    
    public void updateCategory(String currentTitle, String newTitle) throws SQLException
    {
        try (Connection con = conProvider.getConnection())
        {
            String a = "UPDATE Category SET name = (?) WHERE name = (?);";
            PreparedStatement pstmt = con.prepareStatement(a);
            pstmt.setString(1, newTitle);
            pstmt.setString(2, currentTitle);
            pstmt.execute();
            pstmt.close();
            // ikke testet herfra
            ResultSet rs = pstmt.executeQuery("Select * FROM Category;");
            while (rs.next())
            {
                a = "UPDATE Category SET name = (?) WHERE name = (?) ;";
                pstmt = con.prepareStatement(a);
                pstmt.setString(1, newTitle);
                pstmt.setString(2, currentTitle);

                pstmt.execute();
                pstmt.close();
            }
            //hertil
            
        } catch (SQLServerException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public List<Category> getAllCategories()
    {
        List<Category> categories = new ArrayList<>();

        try (Connection con = conProvider.getConnection())
        {

            String a = "SELECT * FROM Category;";
            PreparedStatement pstmt = con.prepareStatement(a);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {

                String title = rs.getString("name");
                Category playlist = new Category(title);
                categories.add(playlist);
            }
            pstmt.close();

        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return categories;
    }
}
