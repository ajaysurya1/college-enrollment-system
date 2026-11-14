package com.university.util;

import com.university.config.DatabaseConnection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSeeder {

    public static void runSqlScript(String filePath) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        String[] commands = content.split(";");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String cmd : commands) {
                String trimmed = cmd.trim();
                if (!trimmed.isEmpty()) {
                    try {
                        stmt.execute(trimmed);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("Applying schema...");
            runSqlScript("database/schema.sql");
            System.out.println("Applying seed data...");
            runSqlScript("database/seed.sql");
            System.out.println("Database seeding completed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

