package messmate.model;

/**
 * Subclass representing a Student user.
 * Demonstrates Syllabus Module 2:
 * - Inheritance (Sub Class extending Super Class User)
 * - The 'super' keyword for constructor chaining
 * - Method Overriding
 */
public class Student extends User {

    // Default Constructor
    public Student() {
        super();
        this.role = "STUDENT";
    }

    // Parameterized Constructor using 'super' keyword
    public Student(int id, String username, String password, String fullName) {
        // Calling superclass constructor (Calling order of constructors)
        super(id, username, password, fullName, "STUDENT");
    }

    // Convenience method: for students, username is the room number
    public String getRoomNumber() {
        return this.username;
    }

    /**
     * Method Overriding (Module 2 Polymorphism)
     * Demonstrates dynamic method dispatch when invoked via a User reference.
     */
    @Override
    public String displayInfo() {
        return "Student: " + fullName + " (Room No: " + username + ")";
    }
}
