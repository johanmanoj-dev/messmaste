package messmate.ui;

import messmate.model.Admin;
import messmate.model.MealBooking;
import messmate.service.BookingService;
import messmate.service.IBookingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Vector;

/**
 * Admin screen for viewing daily meal bookings and total kitchen headcounts.
 * Simple, standard college-level Swing GUI.
 */
public class AdminBookingsFrame extends BaseFrame {

    private Admin currentAdmin;
    private JFrame parentDashboard;
    private IBookingService bookingService;

    private JTextField txtDate;
    private JTable tblBookings;
    private DefaultTableModel tableModel;
    private JLabel lblSummary;

    public AdminBookingsFrame(Admin admin, JFrame parent) {
        super("Messmate - Daily Bookings Summary", 680, 480);
        this.currentAdmin = admin;
        this.parentDashboard = parent;
        this.bookingService = new BookingService();
        initUI();
        loadBookings(LocalDate.now().plusDays(2));
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // 1. Top Filter Row
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.add(new JLabel("Select Date (YYYY-MM-DD):"));
        txtDate = new JTextField(LocalDate.now().plusDays(2).toString(), 10);
        topPanel.add(txtDate);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(txtDate.getText().trim());
                loadBookings(date);
            } catch (DateTimeParseException ex) {
                showError("Invalid date format! Use YYYY-MM-DD.");
            }
        });
        topPanel.add(btnSearch);
        add(topPanel, BorderLayout.NORTH);

        // 2. Center Table using Vector (Module 1 Vector Class & Module 4 JTable)
        Vector<String> columns = new Vector<>();
        columns.add("ID No");
        columns.add("Student Name");
        columns.add("Breakfast");
        columns.add("Lunch");
        columns.add("Snacks");
        columns.add("Dinner");
        columns.add("Total Cost");

        tableModel = new DefaultTableModel(new Vector<Vector<Object>>(), columns) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tblBookings = new JTable(tableModel);
        tblBookings.setRowHeight(22);
        add(new JScrollPane(tblBookings), BorderLayout.CENTER);

        // 3. Bottom Summary Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        lblSummary = new JLabel("Total Students: 0 | Breakfast: 0 | Lunch: 0 | Snacks: 0 | Dinner: 0", JLabel.CENTER);
        lblSummary.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblSummary.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(lblSummary);

        bottomPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        JButton btnBack = new JButton("Back to Dashboard");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(e -> {
            parentDashboard.setVisible(true);
            dispose();
        });
        bottomPanel.add(btnBack);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadBookings(LocalDate date) {
        try {
            List<MealBooking> list = bookingService.getBookingsByDate(date);
            tableModel.setRowCount(0);

            int bCount = 0, lCount = 0, sCount = 0, dCount = 0;

            for (MealBooking b : list) {
                Vector<Object> row = new Vector<>();
                row.add(b.getIdNumber() != null ? b.getIdNumber() : "-");
                row.add(b.getStudentName() != null ? b.getStudentName() : "-");
                row.add(b.isBreakfast() ? "Yes" : "-");
                row.add(b.isLunch() ? "Yes" : "-");
                row.add(b.isSnacks() ? "Yes" : "-");
                row.add(b.isDinner() ? "Yes" : "-");
                row.add("₹ " + String.format("%.2f", b.getTotalCost()));
                tableModel.addRow(row);

                if (b.isBreakfast()) bCount++;
                if (b.isLunch()) lCount++;
                if (b.isSnacks()) sCount++;
                if (b.isDinner()) dCount++;
            }

            lblSummary.setText("Students: " + list.size()
                    + " | Breakfast: " + bCount
                    + " | Lunch: " + lCount
                    + " | Snacks: " + sCount
                    + " | Dinner: " + dCount
            );

        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }
}
