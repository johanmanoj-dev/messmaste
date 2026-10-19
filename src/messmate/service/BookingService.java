package messmate.service;

import messmate.db.DBConnection;
import messmate.exception.DuplicateBookingException;
import messmate.exception.InvalidBookingDateException;
import messmate.model.MealBooking;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for meal bookings.
 * Demonstrates Syllabus:
 * - Module 1: Control structures (if-else, while loop)
 * - Module 2: Method Overloading (calculateBill methods)
 * - Module 3: Implementing an interface, checked exceptions
 * - Module 4: JDBC CRUD operations using PreparedStatement and ResultSet
 */
public class BookingService implements IBookingService {

    /**
     * Books a meal for a student after strictly checking:
     * 1. Date is at least 2 days in the future from today.
     * 2. Date is not already booked.
     */
    @Override
    public boolean bookMeal(MealBooking booking) throws InvalidBookingDateException, DuplicateBookingException, SQLException {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = booking.getMealDate();

        // Business Rule: Booking date must be at least 2 days later than today
        // E.g. If today is Monday (19th), earliest allowed booking is Wednesday (21st)
        long daysAhead = ChronoUnit.DAYS.between(today, targetDate);
        if (daysAhead < 2) {
            throw new InvalidBookingDateException(
                    "Bookings must be made at least 2 days in advance! Earliest allowed date is: "
                    + today.plusDays(2).toString()
            );
        }

        // Business Rule: At least one meal must be selected
        if (!booking.isAnyMealSelected()) {
            throw new InvalidBookingDateException("Please select at least one meal to book.");
        }

        // Check if student has already booked for this target date
        if (isDateAlreadyBooked(booking.getStudentId(), targetDate)) {
            throw new DuplicateBookingException("You have already booked meals for " + targetDate + ".");
        }

        // JDBC CRUD - INSERT (Create Operation)
        String sql = "INSERT INTO bookings (student_id, meal_date, booked_at, "
                + "breakfast, lunch, snacks, dinner, total_cost) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, booking.getStudentId());
            ps.setDate(2, Date.valueOf(booking.getMealDate()));
            ps.setDate(3, Date.valueOf(today));
            ps.setBoolean(4, booking.isBreakfast());
            ps.setBoolean(5, booking.isLunch());
            ps.setBoolean(6, booking.isSnacks());
            ps.setBoolean(7, booking.isDinner());
            ps.setDouble(8, booking.getTotalCost());

            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            if (ps != null) {
                try { ps.close(); } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Checks if a booking already exists for a student on a specific date.
     */
    @Override
    public boolean isDateAlreadyBooked(int studentId, LocalDate mealDate) throws SQLException {
        String sql = "SELECT id FROM bookings WHERE student_id = ? AND meal_date = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setDate(2, Date.valueOf(mealDate));
            rs = ps.executeQuery();
            return rs.next(); // true if record exists
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Fetches all bookings made by a student ordered chronologically (newest first).
     */
    @Override
    public List<MealBooking> getBookingsByStudent(int studentId) throws SQLException {
        List<MealBooking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.full_name, u.username AS room_no FROM bookings b "
                + "JOIN users u ON b.student_id = u.id "
                + "WHERE b.student_id = ? ORDER BY b.meal_date DESC";

        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            rs = ps.executeQuery();

            while (rs.next()) {
                MealBooking mb = mapResultSetToBooking(rs);
                list.add(mb);
            }
            return list;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Fetches all bookings for a specific date (Admin View).
     */
    @Override
    public List<MealBooking> getBookingsByDate(LocalDate date) throws SQLException {
        List<MealBooking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.full_name, u.username AS room_no FROM bookings b "
                + "JOIN users u ON b.student_id = u.id "
                + "WHERE b.meal_date = ? ORDER BY u.username ASC";

        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(date));
            rs = ps.executeQuery();

            while (rs.next()) {
                MealBooking mb = mapResultSetToBooking(rs);
                list.add(mb);
            }
            return list;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Fetches students who booked a specific meal on a given date (Admin Entry View).
     */
    @Override
    public List<MealBooking> getBookingsByDateAndMeal(LocalDate date, String mealType) throws SQLException {
        List<MealBooking> list = new ArrayList<>();
        String mealColumn;

        switch (mealType.toLowerCase()) {
            case "breakfast": mealColumn = "b.breakfast"; break;
            case "lunch": mealColumn = "b.lunch"; break;
            case "evening snacks":
            case "snacks": mealColumn = "b.snacks"; break;
            case "dinner": mealColumn = "b.dinner"; break;
            default: mealColumn = "b.breakfast"; break;
        }

        String sql = "SELECT b.*, u.full_name, u.username AS room_no FROM bookings b "
                + "JOIN users u ON b.student_id = u.id "
                + "WHERE b.meal_date = ? AND " + mealColumn + " = TRUE "
                + "ORDER BY u.username ASC";

        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(date));
            rs = ps.executeQuery();

            while (rs.next()) {
                MealBooking mb = mapResultSetToBooking(rs);
                list.add(mb);
            }
            return list;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Updates meal attendance status (marked by Admin).
     * JDBC CRUD - UPDATE Operation.
     */
    @Override
    public boolean updateAttendance(int bookingId, String mealType, boolean attended) throws SQLException {
        String column;
        switch (mealType.toLowerCase()) {
            case "breakfast": column = "breakfast_attended"; break;
            case "lunch": column = "lunch_attended"; break;
            case "evening snacks":
            case "snacks": column = "snacks_attended"; break;
            case "dinner": column = "dinner_attended"; break;
            default: return false;
        }

        String sql = "UPDATE bookings SET " + column + " = ? WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(sql);
            ps.setBoolean(1, attended);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Method Overloading 1 (Module 2 Polymorphism):
     * Calculates the current month's bill total for a student.
     */
    @Override
    public double calculateBill(int studentId) throws SQLException {
        LocalDate now = LocalDate.now();
        return calculateBill(studentId, now.getMonthValue(), now.getYear());
    }

    /**
     * Method Overloading 2 (Module 2 Polymorphism):
     * Calculates the bill total for a student for a specific month and year.
     */
    @Override
    public double calculateBill(int studentId, int month, int year) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_cost), 0.0) AS total_bill FROM bookings "
                + "WHERE student_id = ? AND MONTH(meal_date) = ? AND YEAR(meal_date) = ?";

        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_bill");
            }
            return 0.0;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Helper to map a JDBC ResultSet row to a MealBooking model object.
     */
    private MealBooking mapResultSetToBooking(ResultSet rs) throws SQLException {
        MealBooking mb = new MealBooking();
        mb.setId(rs.getInt("id"));
        mb.setStudentId(rs.getInt("student_id"));
        mb.setMealDate(rs.getDate("meal_date").toLocalDate());
        mb.setBookedAt(rs.getDate("booked_at").toLocalDate());
        mb.setBreakfast(rs.getBoolean("breakfast"));
        mb.setLunch(rs.getBoolean("lunch"));
        mb.setSnacks(rs.getBoolean("snacks"));
        mb.setDinner(rs.getBoolean("dinner"));
        mb.setBreakfastAttended(rs.getBoolean("breakfast_attended"));
        mb.setLunchAttended(rs.getBoolean("lunch_attended"));
        mb.setSnacksAttended(rs.getBoolean("snacks_attended"));
        mb.setDinnerAttended(rs.getBoolean("dinner_attended"));
        mb.setTotalCost(rs.getDouble("total_cost"));

        // If joined with users table:
        try {
            mb.setStudentName(rs.getString("full_name"));
            mb.setRoomNumber(rs.getString("room_no"));
        } catch (SQLException ignored) {}

        return mb;
    }
}
