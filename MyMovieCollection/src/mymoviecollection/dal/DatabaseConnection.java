/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.dal;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;

/**
 *
 * @author andreas
 */
public class DatabaseConnection {

    private static final String PROP_FILE = "database/database.settings";
    private SQLServerDataSource ds;

    public DatabaseConnection() throws IOException {
        Properties databaseProperties = new Properties();
         databaseProperties.load(new FileInputStream(PROP_FILE));
        ds = new SQLServerDataSource();
        ds.setServerName(databaseProperties.getProperty("servername"));
        ds.setDatabaseName(databaseProperties.getProperty("databasename"));
        ds.setUser(databaseProperties.getProperty("user"));
        ds.setPassword(databaseProperties.getProperty("pw"));
    }
    
        public Connection getConnection() throws SQLServerException
    {
        return ds.getConnection();
    }
}
