package com.example.retroquiz.repository;

import java.sql.*;

/**
 * Cloud Database Connection for Neon.tech PostgreSQL.
 * 
 * This replaces the local SQL Server connection for cloud-based multiplayer.
 * 
 * Neon.tech is simpler and more reliable than Supabase for this use case.
 */
public class CloudDatabaseConnection {

    // ============================================
    // CONFIGURATION - UPDATE THESE VALUES!
    // ============================================

    // Neon.tech PostgreSQL Database (much simpler than Supabase!)
    // This should work immediately without any issues
    private static final String DB_URL = "jdbc:postgresql://ep-steep-butterfly-abeghdks-pooler.eu-west-2.aws.neon.tech/neondb?sslmode=require";
    private static final String DB_USER = "neondb_owner";
    private static final String DB_PASSWORD = "npg_uCy4rWoXxY8F";

    // ============================================

    /**
     * Get connection to cloud database.
     * 
     * @return Connection object or null if failed
     */
    public static Connection getConnection() {
        try {
            // Load PostgreSQL driver
            Class.forName("org.postgresql.Driver");

            // Create connection
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            if (conn != null) {
                System.out.println("✅ Connected to Neon.tech cloud database!");
                initializeTables(conn);
                return conn;
            }

        } catch (ClassNotFoundException e) {
            System.err.println("❌ PostgreSQL driver not found!");
            System.err.println("Make sure you added the dependency to pom.xml");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Cloud database connection failed!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("\nCheck:");
            System.err.println("1. DB_URL is correct");
            System.err.println("2. DB_PASSWORD is correct");
            System.err.println("3. Internet connection is working");
            System.err.println("4. Neon.tech project is active");
        }

        return null;
    }

    /**
     * Close database connection.
     * 
     * @param conn Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("✅ Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    private static void initializeTables(Connection conn) {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id SERIAL PRIMARY KEY, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password_hash VARCHAR(255) NOT NULL, " +
                "is_admin BOOLEAN DEFAULT FALSE, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        
        // Ensure default admin exists
        String createAdmin = "INSERT INTO users (username, password_hash, is_admin) " +
                           "VALUES ('admin', 'admin123', TRUE) " +
                           "ON CONFLICT (username) DO NOTHING;";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createAdmin);
        } catch (SQLException e) {
            System.err.println("Error initializing tables: " + e.getMessage());
        }
    }

    /**
     * Test the database connection.
     * Run this to verify your setup is correct.
     */
    public static void testConnection() {
        System.out.println("Testing Supabase connection...");

        Connection conn = getConnection();

        if (conn != null) {
            try {
                // Test query
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM questions");

                if (rs.next()) {
                    int count = rs.getInt("count");
                    System.out.println("✅ Connection successful!");
                    System.out.println("✅ Found " + count + " questions in database");
                }

                rs.close();
                stmt.close();

            } catch (SQLException e) {
                System.err.println("❌ Test query failed: " + e.getMessage());
            } finally {
                closeConnection(conn);
            }
        } else {
            System.err.println("❌ Connection test failed!");
        }
    }

    /**
     * Main method for testing connection.
     * Run this class directly to test your Supabase setup.
     */
    public static void main(String[] args) {
        testConnection();
    }
}
