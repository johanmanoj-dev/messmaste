package messmate;

import messmate.db.DatabaseInitializer;
import messmate.ui.LoginFrame;

import javax.swing.*;
import java.sql.SQLException;

/**
 * Main application launcher for Messmate.
 * Demonstrates Syllabus:
 * - Module 1: Structure of a simple Java program, main method, command-line arguments
 * - Module 4: Swing threading (SwingUtilities.invokeLater), Look and Feel, JDBC Initialization
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  MESSMATE — Campus Mess Meal Booking System      ");
        System.out.println("  Object Oriented Programming Course Project      ");
        System.out.println("==================================================");

        // Set Cross-platform or System Look and Feel for clean appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Note: Using default Look and Feel (" + e.getMessage() + ")");
        }

        // Initialize Database Tables and Seeds
        try {
            System.out.println("Connecting to MySQL Database...");
            DatabaseInitializer.initialize();
            System.out.println("Database tables and seed users verified successfully.");
        } catch (SQLException e) {
            System.err.println("WARNING: Could not connect to MySQL: " + e.getMessage());
            System.err.println("Ensure MySQL service / XAMPP is running on localhost:3306.");
            System.err.println("You can configure database credentials from the Login screen using 'MySQL Settings'.");
        }

        // Launch Login GUI on Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
