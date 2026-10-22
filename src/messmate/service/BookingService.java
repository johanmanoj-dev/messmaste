package messmate.service;

import messmate.db.DBConnection;
import messmate.exception.DuplicateBookingException;
import messmate.exception.InvalidBookingDateException;
import messmate.model.MealBooking;
import messmate.model.StudentBillSummary;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for meal booking operations.
 * Demonstrates Syllabus:
 * - Module 1: Control structures (if-else, while loop)
 * - Module 2: Method Overloading (calculateBill)
 * - Module 3: Implementing an interface, Custom Checked Exceptions
 * - Module 4: JDBC CRUD Operations with PreparedStatement and ResultSet
 */
public class BookingService implements IBookingService {

    @Override
    public boolean bookMeal(MealBooking booking) throws InvalidBookingDateException, DuplicateBookingException, SQLException {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = booking.getMealDate();

        // 1. Advance Notice Validation (At least 2 days in advance)
        long daysAhead = ChronoUnit.DAYS.between(today, targetDate);
        if (daysAhead < 2) {
            throw new InvalidBookingDateException(
                    "Bookings must be at least 2 days in advance! Earliest date: " + today.plusDays(2)
            );
        }

        // 2. Meal Selection Validation
        if (!booking.isAnyMealSelected()) {
            throw new InvalidBookingDateException("Please select at least one meal.");
        }

        // 3. Duplicate Booking Validation
        if (isDateAlreadyBooked(booking.getStudentId(), targetDate)) {
            throw new DuplicateBookingException("You already have a booking for " + targetDate + ".");
        }

        // 4. JDBC CRUD: INSERT (Create)
        String sql = "INSERT INTO bookings (student_id, meal_date, booked_at, breakfast, lunch, snacks, dinner, total_cost) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, booking.getStudentId());
            ps.setDate(2, Date.valueOf(booking.getMealDate()));
            ps.setDate(3, Date.valueOf(today));
            ps.setBoolean(4, booking.isBreakfast());
            ps.setBoolean(5, booking.isLunch());
            ps.setBoolean(6, booking.isSnacks());
            ps.setBoolean(7, booking.isDinner());
            ps.setDouble(8, booking.getTotalCost());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean isDateAlreadyBooked(int studentId, LocalDate mealDate) throws SQLException {
        String sql = "SELECT id FROM bookings WHERE student_id = ? AND meal_date = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setDate(2, Date.valueOf(mealDate));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true if record already exists
            }
        }
    }

    @Override
    public List<MealBooking> getBookingsByStudent(int studentId) throws SQLException {
        List<MealBooking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE student_id = ? ORDER BY meal_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<MealBooking> getBookingsByDate(LocalDate date) throws SQLException {
        List<MealBooking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.full_name, u.username AS id_no FROM bookings b "
                + "JOIN users u ON b.student_id = u.id "
                + "WHERE b.meal_date = ? ORDER BY u.username ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MealBooking mb = mapResultSet(rs);
                    mb.setStudentName(rs.getString("full_name"));
                    mb.setIdNumber(rs.getString("id_no"));
                    list.add(mb);
                }
            }
        }
        return list;
    }

    @Override
    public List<MealBooking> getBookingsByDateAndMeal(LocalDate date, String mealType) throws SQLException {
        List<MealBooking> list = new ArrayList<>();
        String mealColumn = getColumnForMeal(mealType);

        String sql = "SELECT b.*, u.full_name, u.username AS id_no FROM bookings b "
                + "JOIN users u ON b.student_id = u.id "
                + "WHERE b.meal_date = ? AND " + mealColumn + " = TRUE ORDER BY u.username ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MealBooking mb = mapResultSet(rs);
                    mb.setStudentName(rs.getString("full_name"));
                    mb.setIdNumber(rs.getString("id_no"));
                    list.add(mb);
                }
            }
        }
        return list;
    }

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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, attended);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        }
    }

    // Method Overloading 1 (Module 2 Polymorphism)
    @Override
    public double calculateBill(int studentId) throws SQLException {
        LocalDate now = LocalDate.now();
        return calculateBill(studentId, now.getMonthValue(), now.getYear());
    }

    // Method Overloading 2 (Module 2 Polymorphism)
    @Override
    public double calculateBill(int studentId, int month, int year) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_cost), 0.0) AS total_bill FROM bookings "
                + "WHERE student_id = ? AND MONTH(meal_date) = ? AND YEAR(meal_date) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, month);
            ps.setInt(3, year);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_bill");
                }
            }
        }
        return 0.0;
    }

    @Override
    public List<StudentBillSummary> getMonthlyBillsForAllStudents(int month, int year) throws SQLException {
        List<StudentBillSummary> list = new ArrayList<>();
        String sql = "SELECT "
                + "u.id AS student_id, "
                + "u.username AS id_number, "
                + "u.full_name AS student_name, "
                + "COALESCE(SUM(CASE WHEN b.breakfast = TRUE THEN 1 ELSE 0 END), 0) AS b_count, "
                + "COALESCE(SUM(CASE WHEN b.lunch = TRUE THEN 1 ELSE 0 END), 0) AS l_count, "
                + "COALESCE(SUM(CASE WHEN b.snacks = TRUE THEN 1 ELSE 0 END), 0) AS s_count, "
                + "COALESCE(SUM(CASE WHEN b.dinner = TRUE THEN 1 ELSE 0 END), 0) AS d_count, "
                + "COALESCE(SUM(b.total_cost), 0.0) AS total_bill "
                + "FROM users u "
                + "LEFT JOIN bookings b ON u.id = b.student_id "
                + "     AND MONTH(b.meal_date) = ? "
                + "     AND YEAR(b.meal_date) = ? "
                + "WHERE u.role = 'STUDENT' "
                + "GROUP BY u.id, u.username, u.full_name "
                + "ORDER BY u.username ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, month);
            ps.setInt(2, year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StudentBillSummary sbs = new StudentBillSummary(
                            rs.getInt("student_id"),
                            rs.getString("id_number"),
                            rs.getString("student_name"),
                            rs.getInt("b_count"),
                            rs.getInt("l_count"),
                            rs.getInt("s_count"),
                            rs.getInt("d_count"),
                            rs.getDouble("total_bill")
                    );
                    list.add(sbs);
                }
            }
        }
        return list;
    }

    private String getColumnForMeal(String mealType) {
        switch (mealType.toLowerCase()) {
            case "lunch": return "b.lunch";
            case "evening snacks":
            case "snacks": return "b.snacks";
            case "dinner": return "b.dinner";
            default: return "b.breakfast";
        }
    }

    private MealBooking mapResultSet(ResultSet rs) throws SQLException {
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
        return mb;
    }
}
