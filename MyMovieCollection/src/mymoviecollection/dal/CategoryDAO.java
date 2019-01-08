/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mymoviecollection.be.Category;

/**
 *
 * @author Andreas Svendsen
 */
public class CategoryDAO {
    
    DatabaseConnection conProvider;
    
    public void createCategory(String title)
    {
        try (Connection con = conProvider.getConnection())
        {
            try (PreparedStatement pstmt = con.prepareStatement("INSERT INTO Category (Title) VALUES (?)"))
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
    
    public void deleteCategory(String title) throws SQLException //String skal måske være category fra be laget
    {
        try (Connection con = conProvider.getConnection())
        {
            String a = "DELETE FROM Category WHERE Title = (?);";
            PreparedStatement pstmt = con.prepareStatement(a);
            pstmt.setString(1, title);
            pstmt.execute();
            pstmt.close();

        } catch (SQLServerException ex)
        {
        }
    }
    
    public void updateCategory(String currentTitle, String newTitle) throws SQLException
    {
        try (Connection con = conProvider.getConnection())
        {
            String a = "UPDATE Category SET Title = (?) WHERE Title = (?);";
            PreparedStatement pstmt = con.prepareStatement(a);
            pstmt.setString(1, newTitle);
            pstmt.setString(2, currentTitle);
            pstmt.execute();
            pstmt.close();
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

                String title = rs.getString("title");
                Category playlist = new Category(title);
                categories.add(playlist);
            }

        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return categories;
    }
}
