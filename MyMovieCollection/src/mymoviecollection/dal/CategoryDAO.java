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

    public void createCategory(Category category)
    {
        try (Connection con = conProvider.getConnection())
        {
            try (PreparedStatement pstmt = con.prepareStatement("INSERT INTO Category (name) VALUES (?)"))
            {
                pstmt.setString(1, category.getTitle());
                pstmt.execute();
                pstmt.close();
            }
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }
    public void deleteCategory(String title) throws SQLException
    {
        try (Connection con = conProvider.getConnection())
        {
            String a = "DELETE FROM Category WHERE name = (?);";
            PreparedStatement pstmt = con.prepareStatement(a);
            pstmt.setString(1, title);
            pstmt.execute();
            pstmt.close();
            a = "Select * From catMov;";
            ResultSet rs = con.prepareStatement(a).executeQuery();
            while (rs.next())
            {
                a = "DELETE FROM catMov WHERE Title = (?);";
                pstmt = con.prepareStatement(a);
                pstmt.setString(1, title);
                pstmt.execute();
            }
        } catch (SQLServerException ex)
        {
            throw new SQLException("Delete category Error" + ex);
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
            ResultSet rs = con.prepareStatement("Select * FROM Category;").executeQuery();
            while (rs.next())
            {
                a = "UPDATE Category SET name = (?) WHERE name = (?) ;";
                pstmt = con.prepareStatement(a);
                pstmt.setString(1, newTitle);
                pstmt.setString(2, currentTitle);
                pstmt.execute();
                pstmt.close();
            }
        } catch (SQLServerException ex)
        {
            throw new SQLException("Update category Error" + ex);
        }
    }
    
    public List<Category> getAllCategories() throws SQLException
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
            throw new SQLException("Delete category Error" + ex);
        }
        return categories;
    }
}
