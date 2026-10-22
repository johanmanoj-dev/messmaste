package messmate.model;

/**
 * Model class representing the monthly mess bill summary for a student.
 * Demonstrates Syllabus Module 1:
 * - Encapsulation (private fields, public getters and setters)
 * - Constructors and 'this' keyword
 */
public class StudentBillSummary {

    private int studentId;
    private String idNumber;
    private String studentName;
    private int breakfastCount;
    private int lunchCount;
    private int snacksCount;
    private int dinnerCount;
    private double totalBill;

    public StudentBillSummary() {
    }

    public StudentBillSummary(int studentId, String idNumber, String studentName,
                              int breakfastCount, int lunchCount, int snacksCount,
                              int dinnerCount, double totalBill) {
        this.studentId = studentId;
        this.idNumber = idNumber;
        this.studentName = studentName;
        this.breakfastCount = breakfastCount;
        this.lunchCount = lunchCount;
        this.snacksCount = snacksCount;
        this.dinnerCount = dinnerCount;
        this.totalBill = totalBill;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    @Deprecated
    public String getRoomNumber() {
        return getIdNumber();
    }

    @Deprecated
    public void setRoomNumber(String roomNumber) {
        setIdNumber(roomNumber);
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getBreakfastCount() {
        return breakfastCount;
    }

    public void setBreakfastCount(int breakfastCount) {
        this.breakfastCount = breakfastCount;
    }

    public int getLunchCount() {
        return lunchCount;
    }

    public void setLunchCount(int lunchCount) {
        this.lunchCount = lunchCount;
    }

    public int getSnacksCount() {
        return snacksCount;
    }

    public void setSnacksCount(int snacksCount) {
        this.snacksCount = snacksCount;
    }

    public int getDinnerCount() {
        return dinnerCount;
    }

    public void setDinnerCount(int dinnerCount) {
        this.dinnerCount = dinnerCount;
    }

    public double getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(double totalBill) {
        this.totalBill = totalBill;
    }

    public int getTotalMealsCount() {
        return breakfastCount + lunchCount + snacksCount + dinnerCount;
    }
}
