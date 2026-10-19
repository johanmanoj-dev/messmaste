package messmate.exception;

/**
 * Thrown when a student attempts to book a meal for a date on which
 * they already have an existing booking.
 * Demonstrates Syllabus Module 3 (Custom Exceptions, throw and throws).
 */
public class DuplicateBookingException extends MessMateException {

    public DuplicateBookingException(String message) {
        super(message);
    }
}
