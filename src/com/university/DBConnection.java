package com.university;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    public static Connection getConnection() throws SQLException {
        Properties properties = new Properties();

        try (FileInputStream file = new FileInputStream("db.properties")) {
            properties.load(file);
        } catch (IOException e) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                return DriverManager.getConnection("jdbc:mysql://localhost:3306/university_academic_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", "root", "");
            } catch (Exception ex) {
                throw new SQLException("Could not connect to database: " + ex.getMessage());
            }
        }

        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password", "");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
        }

        return DriverManager.getConnection(url, user, password);
    }
}
