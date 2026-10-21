package messmate.ui;

import messmate.model.MealPricing;
import messmate.model.Student;
import messmate.service.BookingService;
import messmate.service.IBookingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Vector;

/**
 * Screen displaying the student's monthly bill.
 * Simple, standard college-level Swing GUI.
 */
public class BillFrame extends BaseFrame {

    private Student currentStudent;
    private JFrame parentDashboard;
    private IBookingService bookingService;

    public BillFrame(Student student, JFrame parent) {
        super("Messmate - Monthly Bill", 450, 420);
        this.currentStudent = student;
        this.parentDashboard = parent;
        this.bookingService = new BookingService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // 1. Top Panel (Current Month Bill Counter)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        LocalDate now = LocalDate.now();
        String currentMonth = now.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + now.getYear();

        double currentTotal = 0.0;
        try {
            // Method Overloading 1 (Module 2): calculateBill(studentId)
            currentTotal = bookingService.calculateBill(currentStudent.getId());
        } catch (SQLException ex) {
            showError("Error calculating bill: " + ex.getMessage());
        }

        JLabel lblMonth = new JLabel("Current Month: " + currentMonth, JLabel.CENTER);
        lblMonth.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblMonth.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(lblMonth);

        JLabel lblCounter = new JLabel("Total Bill: ₹ " + String.format("%.2f", currentTotal), JLabel.CENTER);
        lblCounter.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblCounter.setForeground(new Color(24, 76, 120));
        lblCounter.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(lblCounter);

        JLabel lblRates = new JLabel(String.format("Fixed Rates: Breakfast ₹%d | Lunch ₹%d | Snacks ₹%d | Dinner ₹%d",
                (int) MealPricing.BREAKFAST_PRICE, (int) MealPricing.LUNCH_PRICE,
                (int) MealPricing.SNACKS_PRICE, (int) MealPricing.DINNER_PRICE), JLabel.CENTER);
        lblRates.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblRates.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(lblRates);

        add(topPanel, BorderLayout.NORTH);

        // 2. Center: Past Months Table
        Vector<String> columns = new Vector<>();
        columns.add("Past Month");
        columns.add("Bill Amount");

        Vector<Vector<Object>> data = new Vector<>();

        // Check previous 4 months (Method Overloading 2: calculateBill(id, month, year))
        for (int i = 1; i <= 4; i++) {
            LocalDate pastDate = now.minusMonths(i);
            try {
                double pastBill = bookingService.calculateBill(
                        currentStudent.getId(), pastDate.getMonthValue(), pastDate.getYear()
                );
                if (pastBill > 0.0) {
                    Vector<Object> row = new Vector<>();
                    row.add(pastDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + pastDate.getYear());
                    row.add("₹ " + String.format("%.2f", pastBill));
                    data.add(row);
                }
            } catch (SQLException ignored) {}
        }

        DefaultTableModel tableModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable tblPastBills = new JTable(tableModel);
        tblPastBills.setRowHeight(22);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Past Months' Billing Records"));
        tablePanel.add(new JScrollPane(tblPastBills), BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // 3. Bottom Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnBack = new JButton("Back to Dashboard");
        btnBack.addActionListener(e -> {
            parentDashboard.setVisible(true);
            dispose();
        });
        bottomPanel.add(btnBack);

        add(bottomPanel, BorderLayout.SOUTH);
    }
}
