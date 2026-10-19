package messmate.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Initializes database tables and seed data upon startup.
 * Demonstrates Syllabus Module 4:
 * - Executing SQL DDL statements (CREATE TABLE)
 * - Statement and ResultSet usage
 * - Try-catch-finally block error management
 */
public class DatabaseInitializer {

    /**
     * Initializes tables and default user seed data.
     * @throws SQLException if a database error occurs
     */
    public static void initialize() throws SQLException {
        Connection conn = DBConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();

            // 1. Create Users Table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(50) NOT NULL UNIQUE, "
                    + "password VARCHAR(50) NOT NULL, "
                    + "full_name VARCHAR(100) NOT NULL, "
                    + "role VARCHAR(20) NOT NULL"
                    + ")";
            stmt.executeUpdate(createUsersTable);

            // 2. Create Bookings Table
            String createBookingsTable = "CREATE TABLE IF NOT EXISTS bookings ("
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
                    + "UNIQUE KEY uk_student_date (student_id, meal_date)"
                    + ")";
            stmt.executeUpdate(createBookingsTable);

            // 3. Check if Users table is empty. If so, insert initial seeds.
            rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM users");
            if (rs.next() && rs.getInt("total") == 0) {
                // Insert Predefined Students and Admins
                stmt.executeUpdate("INSERT INTO users (username, password, full_name, role) VALUES "
                        + "('101', 'pass101', 'Rahul Sharma', 'STUDENT'), "
                        + "('102', 'pass102', 'Sneha Patel', 'STUDENT'), "
                        + "('103', 'pass103', 'Amit Kumar', 'STUDENT'), "
                        + "('104', 'pass104', 'Ananya Nair', 'STUDENT'), "
                        + "('105', 'pass105', 'Rohan Das', 'STUDENT'), "
                        + "('admin', 'admin123', 'Mess Administrator', 'ADMIN'), "
                        + "('999', 'admin123', 'Mess Warden', 'ADMIN')");

                System.out.println("Messmate: Seed users inserted successfully.");
            }

        } finally {
            // Demonstrates proper cleanup of Statement and ResultSet (Module 4 & Module 3 finally)
            if (rs != null) {
                try { rs.close(); } catch (SQLException ignored) {}
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException ignored) {}
            }
        }
    }
}
