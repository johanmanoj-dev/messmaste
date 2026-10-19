package messmate.model;

/**
 * Superclass representing a generic user in the Messmate system.
 * Demonstrates Syllabus Module 1 (Classes, Objects, Encapsulation, Constructors, 'this')
 * and Module 2 (Super Class in Inheritance).
 */
public class User {
    // Encapsulated fields (private / protected for subclass access)
    protected int id;
    protected String username;
    protected String password;
    protected String fullName;
    protected String role; // "STUDENT" or "ADMIN"

    // Default Constructor
    public User() {
    }

    // Parameterized Constructor demonstrating 'this' keyword
    public User(int id, String username, String password, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    // Getters and Setters (Encapsulation)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Method to be overridden by Subclasses (Module 2 Polymorphism / Method Overriding)
     */
    public String displayInfo() {
        return "User: " + fullName + " [Username: " + username + ", Role: " + role + "]";
    }
}
