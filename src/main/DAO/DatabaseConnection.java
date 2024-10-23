package main.DAO;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            String url = prop.getProperty("db.url");
            String username = prop.getProperty("db.username");
            String password = prop.getProperty("db.password");

            Class.forName(prop.getProperty("db.driverClass")); // Загружаем драйвер
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new SQLException("Failed to connect to the database");
        }
    }
}
