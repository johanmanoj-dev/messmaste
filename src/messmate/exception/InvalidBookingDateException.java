package messmate.exception;

/**
 * Thrown when an invalid booking date is selected, such as a date
 * less than 2 days in advance from today or a past date.
 * Demonstrates Syllabus Module 3 (Custom Exceptions, throw and throws).
 */
public class InvalidBookingDateException extends MessMateException {

    public InvalidBookingDateException(String message) {
        super(message);
    }
}
