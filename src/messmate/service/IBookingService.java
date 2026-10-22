package messmate.service;

import messmate.exception.InvalidBookingDateException;
import messmate.exception.DuplicateBookingException;
import messmate.model.MealBooking;
import messmate.model.StudentBillSummary;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Service Interface defining mess meal booking operations.
 * Demonstrates Syllabus Module 3:
 * - Defining an interface
 * - Declaring abstract method contracts
 * - Throws declarations with custom exceptions
 */
public interface IBookingService {

    /**
     * Books a meal for a student after validating business rules.
     * Throws custom checked exceptions if date is invalid or already booked.
     */
    boolean bookMeal(MealBooking booking) throws InvalidBookingDateException, DuplicateBookingException, SQLException;

    /**
     * Checks if a booking already exists for the given student and date.
     */
    boolean isDateAlreadyBooked(int studentId, LocalDate mealDate) throws SQLException;

    /**
     * Fetches all bookings made by a specific student in chronological order.
     */
    List<MealBooking> getBookingsByStudent(int studentId) throws SQLException;

    /**
     * Fetches all student bookings registered for a specific date (Admin view).
     */
    List<MealBooking> getBookingsByDate(LocalDate date) throws SQLException;

    /**
     * Fetches students registered for a specific meal on a date (Admin Entry view).
     */
    List<MealBooking> getBookingsByDateAndMeal(LocalDate date, String mealType) throws SQLException;

    /**
     * Updates meal attendance status (marked by Admin).
     */
    boolean updateAttendance(int bookingId, String mealType, boolean attended) throws SQLException;

    /**
     * Calculates the current month's bill total for a student.
     * Demonstrates Method Overloading (Module 2).
     */
    double calculateBill(int studentId) throws SQLException;

    /**
     * Calculates the bill total for a student for a specific month and year.
     * Demonstrates Method Overloading (Module 2).
     */
    double calculateBill(int studentId, int month, int year) throws SQLException;

    /**
     * Fetches monthly bill summaries for all students for a specific month and year (Admin view).
     */
    List<StudentBillSummary> getMonthlyBillsForAllStudents(int month, int year) throws SQLException;
}
