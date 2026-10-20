package messmate.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Initializes MySQL tables and default users on startup.
 * Demonstrates Syllabus Module 4 (JDBC Statement, DDL Execution).
 */
public class DatabaseInitializer {

    public static void initialize() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Create Users Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(50) NOT NULL UNIQUE, "
                    + "password VARCHAR(50) NOT NULL, "
                    + "full_name VARCHAR(100) NOT NULL, "
                    + "role VARCHAR(20) NOT NULL)");

            // 2. Create Bookings Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS bookings ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "student_id INT NOT NULL, "
                    + "meal_date DATE NOT NULL, "
                    + "booked_at DATE NOT NULL, "
                    + "breakfast BOOLEAN DEFAULT FALSE, "
                    + "lunch BOOLEAN DEFAULT FALSE, "
                    + "snacks BOOLEAN DEFAULT FALSE, "
                    + "dinner BOOLEAN DEFAULT FALSE, "
                    + "breakfast_attended BOOLEAN DEFAULT FALSE, "
                    + "lunch_attended BOOLEAN DEFAULT FALSE, "
                    + "snacks_attended BOOLEAN DEFAULT FALSE, "
                    + "dinner_attended BOOLEAN DEFAULT FALSE, "
                    + "total_cost DOUBLE NOT NULL, "
                    + "FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE, "
                    + "UNIQUE KEY uk_student_date (student_id, meal_date))");

            // 3. Seed users using INSERT IGNORE (automatically adds any new users, skips existing ones)
            stmt.executeUpdate("INSERT IGNORE INTO users (username, password, full_name, role) VALUES "
                    + "('101', 'pass101', 'Rahul Sharma', 'STUDENT'), "
                    + "('102', 'pass102', 'Sneha Patel', 'STUDENT'), "
                    + "('103', 'pass103', 'Amit Kumar', 'STUDENT'), "
                    + "('104', 'pass104', 'Gundalpettu Goutham GG', 'STUDENT'), "
                    + "('105', 'pass105', 'Rohan Das', 'STUDENT'), "
                    + "('admin', 'admin123', 'Mess Administrator', 'ADMIN'), "
                    + "('106', 'pass104', 'Gundalpettu Goutham GG', 'STUDENT'), "
                    + "('107', 'pass102', 'Korifia.kollam KK', 'STUDENT'), "
                    + "('999', 'admin123', 'Mess Warden', 'ADMIN')"
                    + "('108', 'pass102', 'Chokili salman', 'STUDENT'), "
);
                    
        }
    }
}
