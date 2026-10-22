package messmate.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Initializes MySQL tables and default users on startup.
 * Demonstrates Syllabus Module 4 (JDBC Statement, DDL Execution, Batch PreparedStatement).
 */
public class DatabaseInitializer {

    /**
     * Student admission roster: {Admission No / ID Number, Full Name}
     * Sourced from CSE 2025-2029 A (S1) official class list.
     */
    public static final String[][] STUDENTS = {
        {"25CS008", "ABHINAV SHINOJ"},
        {"25CS012", "ABHISHEK P NAIR"},
        {"25CS013", "ABIYA PRAKASH A B"},
        {"25CS017", "ADWAITH ANIL"},
        {"25CS020", "ADWAITH D NAIR"},
        {"25CS021", "AFIA S"},
        {"25CS031", "ALAN BAIJU"},
        {"25CS033", "ALBIN ANTONY"},
        {"25CS034", "ALBIN BINU"},
        {"25CS040", "ALONA CINTO"},
        {"25CS044", "AMITHA P A"},
        {"25CS050", "ANNS MARIA GINS"},
        {"25CS053", "ARCHANA SUDEEP"},
        {"25CS055", "ASHIN JOSEPH SOJI"},
        {"25CS058", "ASWIN BIJU"},
        {"25CS062", "ATHUL R NAIR"},
        {"25CS063", "AUGUSTINE JOMON"},
        {"25CS066", "CHRIS JOSEPH SHINE"},
        {"25CS069", "CYRIAC JOSE"},
        {"25CS077", "DIYA GOPAKUMAR"},
        {"25CS080", "DONA VINCENT"},
        {"25CS082", "EFINOVA SABU"},
        {"25CS083", "ELAINE ROSE SUNIL"},
        {"25CS085", "ELSA ROSE JIMMY"},
        {"25CS087", "EMMANUEL THOMAS ARUN"},
        {"25CS088", "EVANGELY MARIAM SHANTIS"},
        {"25CS093", "GANGA P G"},
        {"25CS094", "GAUTHAM DAS"},
        {"25CS095", "GEO MATHEW SAJU"},
        {"25CS096", "GEORGE THOMAS"},
        {"25CS099", "GOUTHAM KRISHNA V V"},
        {"25CS101", "HARIJAYANTH S"},
        {"25CS102", "HARITHA ROY"},
        {"25CS104", "IRIN GEORGE"},
        {"25CS106", "JAIDIN MEJE"},
        {"25CS108", "JEES REJI"},
        {"25CS110", "JEREMY JOJI"},
        {"25CS111", "JEROME ABRAHAM PIOUS"},
        {"25CS112", "JESTO JOHNSON"},
        {"25CS113", "JESVIN JOIES"},
        {"25CS114", "JEWEL MARIYA VARGHESE"},
        {"25CS117", "JOEL JOSE"},
        {"25CS118", "JOEL JOSEPH"},
        {"25CS121", "JOHAN P MANOJ"},
        {"25CS123", "JOICE BENNY"},
        {"25CS124", "JOSEPH MICHAEL"},
        {"25CS126", "JOSU JAISON"},
        {"25CS130", "KARTHIK S GOPAL"},
        {"25CS133", "KRISHNAJITH A"},
        {"25CS136", "LEKSHMI SREEKUMAR"},
        {"25CS138", "LIYA TOMY"},
        {"25CS140", "MADHAV SURESH"},
        {"25CS146", "MICHAEL JAMES"},
        {"25CS147", "MILANA MARIA MATHEW"},
        {"25CS149", "MINNA AUGUSTIN"},
        {"25CS150", "MIRON VINCENT"},
        {"25CS152", "MRUDUL R NAIR"},
        {"25CS158", "NIRANJAN MANOJ"},
        {"25CS161", "NISSIMOL SABU"},
        {"25CS171", "PRIYADARSHINI M R"},
        {"25CS175", "RHEA THOMAS"},
        {"25CS178", "RONY SIBY"},
        {"25CS183", "SALMAN S"},
        {"25CS192", "SIVAPRIYA M"},
        {"25CS196", "SREEHARI S"},
        {"25CS198", "THEJUS TOM PIOUS"},
        {"25CS200", "TISSA ROSE MATHEW"},
        {"25CS204", "VYGA SAJIKUMAR"}
    };

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

            // 3. Remove obsolete room-number test accounts if present
            stmt.executeUpdate("DELETE FROM users WHERE username IN ('101', '102', '103', '104', '105', '106', '107', '108')");

            // 4. Seed Admin users using INSERT IGNORE
            stmt.executeUpdate("INSERT IGNORE INTO users (username, password, full_name, role) VALUES "
                    + "('admin', 'admin123', 'Mess Administrator', 'ADMIN'), "
                    + "('999', 'admin123', 'Mess Warden', 'ADMIN')");

            // 5. Seed Students using Batch PreparedStatement
            // ID Number = Admission No, Password = ID Number, Full Name = Name
            String insertStudentSql = "INSERT IGNORE INTO users (username, password, full_name, role) VALUES (?, ?, ?, 'STUDENT')";
            try (PreparedStatement ps = conn.prepareStatement(insertStudentSql)) {
                for (String[] student : STUDENTS) {
                    ps.setString(1, student[0]); // ID number (admission number)
                    ps.setString(2, student[0]); // password same as ID number
                    ps.setString(3, student[1]); // full name
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    public static void main(String[] args) {
        try {
            initialize();
            System.out.println("Database initialization completed successfully!");
        } catch (SQLException ex) {
            System.err.println("Database initialization failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
