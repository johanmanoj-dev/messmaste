package messmate.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages JDBC connection establishment to MySQL.
 * Demonstrates Syllabus:
 * - Module 2: Static Members (Singleton-style connection access)
 * - Module 3: Exception Handling (try-catch-finally, checked exceptions SQLException)
 * - Module 4: Developing Database Applications using JDBC (Steps, Connection Establishment)
 */
public class DBConnection {

    private static final String CONFIG_FILE = "db.properties";
    private static String host = "localhost";
    private static int port = 3306;
    private static String dbName = "messmate_db";
    private static String username = "root";
    private static String password = ""; // Default empty for XAMPP / local setups

    private static Connection connection = null;

    static {
        loadProperties();
    }

    private DBConnection() {
        // Private constructor prevents external instantiation
    }

    /**
     * Loads database credentials from db.properties if it exists.
     */
    public static void loadProperties() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Properties props = new Properties();
                props.load(fis);
                host = props.getProperty("db.host", host);
                port = Integer.parseInt(props.getProperty("db.port", String.valueOf(port)));
                dbName = props.getProperty("db.name", dbName);
                username = props.getProperty("db.user", username);
                password = props.getProperty("db.password", password);
            } catch (Exception e) {
                System.err.println("Note: Using default MySQL settings (" + e.getMessage() + ")");
            }
        }
    }

    /**
     * Saves updated database credentials to db.properties.
     */
    public static void saveProperties(String newHost, int newPort, String newDbName, String newUser, String newPass) {
        host = newHost;
        port = newPort;
        dbName = newDbName;
        username = newUser;
        password = newPass;

        Properties props = new Properties();
        props.setProperty("db.host", host);
        props.setProperty("db.port", String.valueOf(port));
        props.setProperty("db.name", dbName);
        props.setProperty("db.user", username);
        props.setProperty("db.password", password);

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Messmate Database Settings");
        } catch (IOException e) {
            System.err.println("Failed to save db.properties: " + e.getMessage());
        }

        // Reset existing connection so new settings take effect
        closeConnection();
    }

    /**
     * Establishes and returns an active JDBC Connection.
     * Demonstrates Module 4: Common JDBC Components & Connection Establishment
     *
     * @return Connection to MySQL database
     * @throws SQLException if database connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Check if existing connection is still valid and open
            if (connection != null && !connection.isClosed()) {
                return connection;
            }

            // Step 1: Load MySQL JDBC Driver (Checked Exception: ClassNotFoundException)
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                // Also support legacy driver name if older connector is used
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException ex) {
                    throw new SQLException("MySQL JDBC Driver not found in classpath. Ensure mysql-connector-j.jar is in lib folder.", ex);
                }
            }

            // Step 2: Formulate JDBC Connection URL
            // createDatabaseIfNotExist=true automatically provisions messmate_db
            String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                    + "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

            // Step 3: Establish Connection using DriverManager
            connection = DriverManager.getConnection(url, username, password);
            return connection;

        } catch (SQLException e) {
            // Re-throw checked exception for UI/Caller to handle gracefully
            throw e;
        }
    }

    /**
     * Closes the active database connection if open.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    // Getters for configuration values
    public static String getHost() { return host; }
    public static int getPort() { return port; }
    public static String getDbName() { return dbName; }
    public static String getUsername() { return username; }
    public static String getPassword() { return password; }
}
