package messmate.exception;

/**
 * Base custom checked exception for the Messmate application.
 * Demonstrates Syllabus Module 3 (Exception Handling - Custom Exceptions, Checked Exceptions).
 */
public class MessMateException extends Exception {

    public MessMateException(String message) {
        super(message);
    }

    public MessMateException(String message, Throwable cause) {
        super(message, cause);
    }
}
