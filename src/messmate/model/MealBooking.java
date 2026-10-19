package messmate.model;

import java.time.LocalDate;

/**
 * Model class representing a meal booking record.
 * Demonstrates Syllabus Module 1:
 * - Encapsulation (private fields, public getters and setters)
 * - Constructors and 'this' keyword
 */
public class MealBooking {
    private int id;
    private int studentId;
    private String studentName; // Joined for display convenience
    private String roomNumber;  // Joined for display convenience
    private LocalDate mealDate;
    private LocalDate bookedAt;
    private boolean breakfast;
    private boolean lunch;
    private boolean snacks;
    private boolean dinner;
    private boolean breakfastAttended;
    private boolean lunchAttended;
    private boolean snacksAttended;
    private boolean dinnerAttended;
    private double totalCost;

    // Default Constructor
    public MealBooking() {
    }

    // Parameterized Constructor for creating a new booking
    public MealBooking(int studentId, LocalDate mealDate, LocalDate bookedAt,
                       boolean breakfast, boolean lunch, boolean snacks, boolean dinner) {
        this.studentId = studentId;
        this.mealDate = mealDate;
        this.bookedAt = bookedAt;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.snacks = snacks;
        this.dinner = dinner;
        this.totalCost = MealPricing.calculateCost(breakfast, lunch, snacks, dinner);
    }

    // Full Parameterized Constructor
    public MealBooking(int id, int studentId, LocalDate mealDate, LocalDate bookedAt,
                       boolean breakfast, boolean lunch, boolean snacks, boolean dinner,
                       boolean breakfastAttended, boolean lunchAttended,
                       boolean snacksAttended, boolean dinnerAttended, double totalCost) {
        this.id = id;
        this.studentId = studentId;
        this.mealDate = mealDate;
        this.bookedAt = bookedAt;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.snacks = snacks;
        this.dinner = dinner;
        this.breakfastAttended = breakfastAttended;
        this.lunchAttended = lunchAttended;
        this.snacksAttended = snacksAttended;
        this.dinnerAttended = dinnerAttended;
        this.totalCost = totalCost;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public LocalDate getMealDate() {
        return mealDate;
    }

    public void setMealDate(LocalDate mealDate) {
        this.mealDate = mealDate;
    }

    public LocalDate getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(LocalDate bookedAt) {
        this.bookedAt = bookedAt;
    }

    public boolean isBreakfast() {
        return breakfast;
    }

    public void setBreakfast(boolean breakfast) {
        this.breakfast = breakfast;
    }

    public boolean isLunch() {
        return lunch;
    }

    public void setLunch(boolean lunch) {
        this.lunch = lunch;
    }

    public boolean isSnacks() {
        return snacks;
    }

    public void setSnacks(boolean snacks) {
        this.snacks = snacks;
    }

    public boolean isDinner() {
        return dinner;
    }

    public void setDinner(boolean dinner) {
        this.dinner = dinner;
    }

    public boolean isBreakfastAttended() {
        return breakfastAttended;
    }

    public void setBreakfastAttended(boolean breakfastAttended) {
        this.breakfastAttended = breakfastAttended;
    }

    public boolean isLunchAttended() {
        return lunchAttended;
    }

    public void setLunchAttended(boolean lunchAttended) {
        this.lunchAttended = lunchAttended;
    }

    public boolean isSnacksAttended() {
        return snacksAttended;
    }

    public void setSnacksAttended(boolean snacksAttended) {
        this.snacksAttended = snacksAttended;
    }

    public boolean isDinnerAttended() {
        return dinnerAttended;
    }

    public void setDinnerAttended(boolean dinnerAttended) {
        this.dinnerAttended = dinnerAttended;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public boolean isAnyMealSelected() {
        return breakfast || lunch || snacks || dinner;
    }

    /**
     * Checks if all meals selected for this booking have been attended.
     */
    public boolean isAttended() {
        if (!isAnyMealSelected()) {
            return false;
        }
        if (breakfast && !breakfastAttended) return false;
        if (lunch && !lunchAttended) return false;
        if (snacks && !snacksAttended) return false;
        if (dinner && !dinnerAttended) return false;
        return true;
    }

    /**
     * Determines booking state (Strictly two states):
     * 1. "Completed" -> If attended OR if the meal day has passed.
     * 2. "Active"    -> If booked and not yet attended.
     */
    public String getStatus() {
        boolean dayPassed = (mealDate != null && mealDate.isBefore(LocalDate.now()));
        boolean attended = isAttended();

        if (attended || dayPassed) {
            return "Completed";
        }
        return "Active";
    }
}
