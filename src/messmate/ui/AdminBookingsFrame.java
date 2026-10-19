package messmate.ui;

import messmate.model.Admin;
import messmate.model.MealBooking;
import messmate.service.BookingService;
import messmate.service.IBookingService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Vector;

/**
 * Admin screen for inspecting daily meal bookings and total demand counts.
 * Demonstrates Syllabus:
 * - Module 1: Vector class (Basics only used for DefaultTableModel headers & rows), Iteration statements
 * - Module 2: Inheritance (BaseFrame)
 * - Module 4: JTable, JScrollPane, JTextField, JButton, Layout Managers
 */
public class AdminBookingsFrame extends BaseFrame {

    private Admin currentAdmin;
    private JFrame parentDashboard;
    private IBookingService bookingService;

    private JTextField txtFilterDate;
    private JButton btnLoadBookings;
    private JTable tblBookings;
    private DefaultTableModel tableModel;

    // Summary labels
    private JLabel lblTotalStudents;
    private JLabel lblTotalBreakfast;
    private JLabel lblTotalLunch;
    private JLabel lblTotalSnacks;
    private JLabel lblTotalDinner;
    private JLabel lblTotalRevenue;

    public AdminBookingsFrame(Admin admin, JFrame parent) {
        super("Messmate — Daily Meal Demand Summary", 800, 620);
        this.currentAdmin = admin;
        this.parentDashboard = parent;
        this.bookingService = new BookingService();
        initUI();
        loadBookingsForDate(LocalDate.now().plusDays(2)); // Default to sample future date
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createHeaderPanel(
                "Daily Meal Demand & Bookings",
                "View registered students and cumulative meal counts for kitchen planning"
        );
        add(headerPanel, BorderLayout.NORTH);

        // Top Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        filterBar.setBackground(BG_LIGHT);

        filterBar.add(new JLabel("Select Date (YYYY-MM-DD):"));
        txtFilterDate = new JTextField(LocalDate.now().plusDays(2).toString(), 10);
        txtFilterDate.setFont(BODY_FONT);
        filterBar.add(txtFilterDate);

        btnLoadBookings = createPrimaryButton("View Bookings");
        btnLoadBookings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    LocalDate date = LocalDate.parse(txtFilterDate.getText().trim());
                    loadBookingsForDate(date);
                } catch (DateTimeParseException ex) {
                    showError("Invalid date format. Please use YYYY-MM-DD.");
                }
            }
        });
        filterBar.add(btnLoadBookings);

        add(filterBar, BorderLayout.NORTH);

        // Center Table using Vector (Module 1 Vector Class Basics)
        Vector<String> columns = new Vector<>();
        columns.add("Room No");
        columns.add("Student Name");
        columns.add("Breakfast (✓/-)");
        columns.add("Lunch (✓/-)");
        columns.add("Snacks (✓/-)");
        columns.add("Dinner (✓/-)");
        columns.add("Total (₹)");

        Vector<Vector<Object>> data = new Vector<>();
        tableModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only view
            }
        };

        tblBookings = new JTable(tableModel);
        tblBookings.setFont(BODY_FONT);
        tblBookings.setRowHeight(26);
        tblBookings.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tblBookings.getTableHeader().setBackground(new Color(230, 238, 248));

        // Center align columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblBookings.getColumnCount(); i++) {
            if (i != 1) { // keep student name left aligned
                tblBookings.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        JScrollPane scrollPane = new JScrollPane(tblBookings);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        // Main Center Panel (Filter + Table)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BG_LIGHT);
        centerPanel.add(filterBar, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Summary Panel
        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBackground(BG_LIGHT);

        JPanel summaryCard = new JPanel(new GridLayout(2, 3, 15, 8));
        summaryCard.setBackground(CARD_BG);
        summaryCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 210, 225), 1),
                        "Kitchen Daily Meal Summary"
                ),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        lblTotalStudents = new JLabel("Total Students: 0");
        lblTotalBreakfast = new JLabel("Total Breakfast: 0");
        lblTotalLunch = new JLabel("Total Lunch: 0");
        lblTotalSnacks = new JLabel("Total Evening Snacks: 0");
        lblTotalDinner = new JLabel("Total Dinner: 0");
        lblTotalRevenue = new JLabel("Expected Revenue: ₹0.00");

        Font sumFont = new Font("SansSerif", Font.BOLD, 13);
        lblTotalStudents.setFont(sumFont);
        lblTotalBreakfast.setFont(sumFont);
        lblTotalLunch.setFont(sumFont);
        lblTotalSnacks.setFont(sumFont);
        lblTotalDinner.setFont(sumFont);
        lblTotalRevenue.setFont(sumFont);

        lblTotalRevenue.setForeground(ACCENT_COLOR);

        summaryCard.add(lblTotalStudents);
        summaryCard.add(lblTotalBreakfast);
        summaryCard.add(lblTotalLunch);
        summaryCard.add(lblTotalSnacks);
        summaryCard.add(lblTotalDinner);
        summaryCard.add(lblTotalRevenue);

        bottomContainer.add(summaryCard, BorderLayout.CENTER);

        // Back Button
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonRow.setBackground(BG_LIGHT);
        JButton btnBack = createButton("Back to Dashboard", new Color(120, 130, 140), Color.WHITE);
        btnBack.addActionListener(e -> {
            parentDashboard.setVisible(true);
            dispose();
        });
        buttonRow.add(btnBack);
        bottomContainer.add(buttonRow, BorderLayout.SOUTH);

        add(bottomContainer, BorderLayout.SOUTH);
    }

    /**
     * Queries database for the given date and populates table using Vectors.
     * Demonstrates Module 1 (Vectors) and Module 4 (JDBC ResultSet processing).
     */
    private void loadBookingsForDate(LocalDate date) {
        try {
            List<MealBooking> bookings = bookingService.getBookingsByDate(date);

            // Clear table data
            tableModel.setRowCount(0);

            int countStudents = bookings.size();
            int countBreakfast = 0;
            int countLunch = 0;
            int countSnacks = 0;
            int countDinner = 0;
            double sumRevenue = 0.0;

            for (MealBooking b : bookings) {
                // Vector representing each row (Module 1 Vector class)
                Vector<Object> row = new Vector<>();
                row.add(b.getRoomNumber() != null ? b.getRoomNumber() : "-");
                row.add(b.getStudentName() != null ? b.getStudentName() : "-");
                row.add(b.isBreakfast() ? "✓ Yes" : "—");
                row.add(b.isLunch() ? "✓ Yes" : "—");
                row.add(b.isSnacks() ? "✓ Yes" : "—");
                row.add(b.isDinner() ? "✓ Yes" : "—");
                row.add("₹" + String.format("%.2f", b.getTotalCost()));

                tableModel.addRow(row);

                if (b.isBreakfast()) countBreakfast++;
                if (b.isLunch()) countLunch++;
                if (b.isSnacks()) countSnacks++;
                if (b.isDinner()) countDinner++;
                sumRevenue += b.getTotalCost();
            }

            // Update summary labels
            lblTotalStudents.setText("Total Students: " + countStudents);
            lblTotalBreakfast.setText("Total Breakfast: " + countBreakfast);
            lblTotalLunch.setText("Total Lunch: " + countLunch);
            lblTotalSnacks.setText("Total Evening Snacks: " + countSnacks);
            lblTotalDinner.setText("Total Dinner: " + countDinner);
            lblTotalRevenue.setText("Expected Revenue: ₹" + String.format("%.2f", sumRevenue));

        } catch (SQLException ex) {
            showError("Failed to fetch bookings for date " + date + ": " + ex.getMessage());
        }
    }
}
