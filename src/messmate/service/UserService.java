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
 * Service class handling user authentication and lookups.
 * Demonstrates Syllabus:
 * - Module 2: Polymorphism & Dynamic Method Dispatch (Instantiating Student or Admin as User)
 * - Module 4: JDBC PreparedStatement and ResultSet queries
 */
public class UserService {

    /**
     * Authenticates credentials and returns a polymorphic User object (Student or Admin).
     * @param username Room Number for student, or admin ID
     * @param password Password
     * @return User object (Student or Admin), or null if authentication fails
     * @throws SQLException on database error
     */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, username.trim());
            ps.setString(2, password.trim());
            rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String user = rs.getString("username");
                String pass = rs.getString("password");
                String name = rs.getString("full_name");
                String role = rs.getString("role");

                // Dynamic Method Dispatch / Polymorphism (Module 2)
                if ("STUDENT".equalsIgnoreCase(role)) {
                    return new Student(id, user, pass, name);
                } else if ("ADMIN".equalsIgnoreCase(role)) {
                    return new Admin(id, user, pass, name);
                } else {
                    return new User(id, user, pass, name, role);
                }
            }
            return null; // Invalid credentials

        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Finds a user by ID.
     */
    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String user = rs.getString("username");
                String pass = rs.getString("password");
                String name = rs.getString("full_name");
                String role = rs.getString("role");

                if ("STUDENT".equalsIgnoreCase(role)) {
                    return new Student(id, user, pass, name);
                } else {
                    return new Admin(id, user, pass, name);
                }
            }
            return null;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        }
    }
}
