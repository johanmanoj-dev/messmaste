package messmate.model;

/**
 * Subclass representing an Administrator user.
 * Demonstrates Syllabus Module 2:
 * - Inheritance (Sub Class extending Super Class User)
 * - The 'super' keyword
 * - Method Overriding
 */
public class Admin extends User {

    // Default Constructor
    public Admin() {
        super();
        this.role = "ADMIN";
    }

    // Parameterized Constructor using 'super' keyword
    public Admin(int id, String username, String password, String fullName) {
        super(id, username, password, fullName, "ADMIN");
    }

    /**
     * Method Overriding (Module 2 Polymorphism)
     */
    @Override
    public String displayInfo() {
        return "Administrator: " + fullName + " (Admin ID: " + username + ")";
    }
}
