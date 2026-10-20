package messmate.service;

import messmate.db.DBConnection;
import messmate.model.Admin;
import messmate.model.Student;
import messmate.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Service handling user login and retrieval.
 * Demonstrates Syllabus:
 * - Module 2: Polymorphism (Returning Student or Admin as User)
 * - Module 4: JDBC PreparedStatement and ResultSet
 */
public class UserService {

    /**
     * Authenticates user and returns a Student or Admin instance.
     */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, password.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String user = rs.getString("username");
                    String pass = rs.getString("password");
                    String name = rs.getString("full_name");
                    String role = rs.getString("role");

                    // Dynamic Method Dispatch / Polymorphism (Module 2)
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        return new Admin(id, user, pass, name);
                    } else {
                        return new Student(id, user, pass, name);
                    }
                }
            }
        }
        return null; // Invalid credentials
    }
}
