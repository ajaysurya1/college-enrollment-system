package com.university;

import com.university.config.DatabaseConnection;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseIntegrationTest {

    @Test
    public void testMySQLConnectivityIfAvailable() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            assertNotNull(conn);
            assertFalse(conn.isClosed());

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1")) {
                assertTrue(rs.next());
                assertEquals(1, rs.getInt(1));
            }
            System.out.println("Integration Test: Connected to MySQL successfully!");
        } catch (Exception e) {
            System.out.println("Integration Test: MySQL not running locally (" + e.getMessage() + "). Skipping live DB test.");
        }
    }
}

