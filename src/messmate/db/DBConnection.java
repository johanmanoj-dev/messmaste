package messmate.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database connection to MySQL.
 * Demonstrates Syllabus:
 * - Module 2: Static Members
 * - Module 4: JDBC Connection Establishment (DriverManager, Connection)
 */
public class DBConnection {

    private static String host = "localhost";
    private static int port = 3306;
    private static String dbName = "messmate_db";
    private static String username = "root";
    private static String password = ""; // Default empty for XAMPP

    private DBConnection() {}

    /**
     * Establishes and returns a new MySQL JDBC connection.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver not found: " + e.getMessage());
        }
        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                + "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Updates MySQL credentials if configured by the user via the UI.
     */
    public static void saveProperties(String newHost, int newPort, String newDb, String newUser, String newPass) {
        host = newHost;
        port = newPort;
        dbName = newDb;
        username = newUser;
        password = newPass;
    }

    public static String getHost() { return host; }
    public static int getPort() { return port; }
    public static String getDbName() { return dbName; }
    public static String getUsername() { return username; }
    public static String getPassword() { return password; }
}
