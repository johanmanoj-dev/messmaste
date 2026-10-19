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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Screen for marking student meal consumption attendance.
 * Demonstrates Syllabus:
 * - Module 1: Control structures, Vector data manipulation
 * - Module 2: Inheritance (BaseFrame)
 * - Module 4: JComboBox (Drop-down), JTable with Boolean Checkbox cells, Event Handling, JDBC Update
 */
public class AdminEntryFrame extends BaseFrame {

    private Admin currentAdmin;
    private JFrame parentDashboard;
    private IBookingService bookingService;

    private JTextField txtEntryDate;
    private JComboBox<String> cmbMealType;
    private JButton btnLoadStudents;
    private JTable tblAttendance;
    private DefaultTableModel tableModel;
    private JButton btnSaveAttendance;

    // Keep track of loaded booking models for batch saving
    private List<MealBooking> currentLoadedBookings = new ArrayList<>();

    public AdminEntryFrame(Admin admin, JFrame parent) {
        super("Messmate — Meal Entry & Attendance Tracker", 700, 580);
        this.currentAdmin = admin;
        this.parentDashboard = parent;
        this.bookingService = new BookingService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createHeaderPanel(
                "Meal Entry & Attendance Tracker",
                "Select meal session and mark student attendance upon dining hall entry"
        );
        add(headerPanel, BorderLayout.NORTH);

        // Top Filter Bar (Date & Meal Dropdown)
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        filterBar.setBackground(BG_LIGHT);

        filterBar.add(new JLabel("Date (YYYY-MM-DD):"));
        txtEntryDate = new JTextField(LocalDate.now().plusDays(2).toString(), 10);
        txtEntryDate.setFont(BODY_FONT);
        filterBar.add(txtEntryDate);

        filterBar.add(new JLabel("Meal Type:"));
        String[] meals = {"Breakfast", "Lunch", "Evening Snacks", "Dinner"};
        cmbMealType = new JComboBox<>(meals);
        cmbMealType.setFont(BODY_FONT);
        filterBar.add(cmbMealType);

        btnLoadStudents = createPrimaryButton("Load Students");
        btnLoadStudents.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadStudentsForSelectedSession();
            }
        });
        filterBar.add(btnLoadStudents);

        add(filterBar, BorderLayout.NORTH);

        // Center Table with Interactive Boolean Checkboxes
        Vector<String> columns = new Vector<>();
        columns.add("Booking ID");
        columns.add("Room No");
        columns.add("Student Name");
        columns.add("Meal Consumed (Attended)");

        tableModel = new DefaultTableModel(new Vector<Vector<Object>>(), columns) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) {
                    return Boolean.class; // Natively renders as JCheckBox in JTable!
                }
                return Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only the attendance checkbox column is editable
            }
        };

        tblAttendance = new JTable(tableModel);
        tblAttendance.setFont(BODY_FONT);
        tblAttendance.setRowHeight(28);
        tblAttendance.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tblAttendance.getTableHeader().setBackground(new Color(230, 238, 248));

        // Center align Room No
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblAttendance.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblAttendance.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblAttendance.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tblAttendance.getColumnModel().getColumn(2).setPreferredWidth(250);
        tblAttendance.getColumnModel().getColumn(3).setPreferredWidth(160);

        JScrollPane scrollPane = new JScrollPane(tblAttendance);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BG_LIGHT);
        centerPanel.add(filterBar, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(BG_LIGHT);
        bottomBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        btnSaveAttendance = createAccentButton("Save Attendance to Database");
        btnSaveAttendance.addActionListener(new SaveAttendanceListener());
        bottomBar.add(btnSaveAttendance, BorderLayout.WEST);

        JButton btnBack = createButton("Back to Dashboard", new Color(120, 130, 140), Color.WHITE);
        btnBack.addActionListener(e -> {
            parentDashboard.setVisible(true);
            dispose();
        });
        bottomBar.add(btnBack, BorderLayout.EAST);

        add(bottomBar, BorderLayout.SOUTH);
    }

    /**
     * Loads the registered students for the selected date and meal type.
     */
    private void loadStudentsForSelectedSession() {
        String dateStr = txtEntryDate.getText().trim();
        String selectedMeal = (String) cmbMealType.getSelectedItem();

        try {
            LocalDate date = LocalDate.parse(dateStr);
            currentLoadedBookings = bookingService.getBookingsByDateAndMeal(date, selectedMeal);

            tableModel.setRowCount(0); // clear existing rows

            if (currentLoadedBookings.isEmpty()) {
                showInfo("No students are registered for " + selectedMeal + " on " + date + ".");
                return;
            }

            for (MealBooking b : currentLoadedBookings) {
                Vector<Object> row = new Vector<>();
                row.add(b.getId());
                row.add(b.getRoomNumber() != null ? b.getRoomNumber() : "-");
                row.add(b.getStudentName() != null ? b.getStudentName() : "-");

                // Get current attendance status for this specific meal
                boolean attended = false;
                if ("Breakfast".equalsIgnoreCase(selectedMeal)) attended = b.isBreakfastAttended();
                else if ("Lunch".equalsIgnoreCase(selectedMeal)) attended = b.isLunchAttended();
                else if ("Evening Snacks".equalsIgnoreCase(selectedMeal)) attended = b.isSnacksAttended();
                else if ("Dinner".equalsIgnoreCase(selectedMeal)) attended = b.isDinnerAttended();

                row.add(Boolean.valueOf(attended));
                tableModel.addRow(row);
            }

        } catch (DateTimeParseException ex) {
            showError("Invalid date format. Please use YYYY-MM-DD.");
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }

    /**
     * Inner Class for saving attendance back to MySQL.
     * Demonstrates Module 4 JDBC UPDATE operations.
     */
    private class SaveAttendanceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tableModel.getRowCount() == 0) {
                showError("No student records loaded to save.");
                return;
            }

            String selectedMeal = (String) cmbMealType.getSelectedItem();
            int updatedCount = 0;

            try {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    int bookingId = (int) tableModel.getValueAt(i, 0);
                    Boolean attended = (Boolean) tableModel.getValueAt(i, 3);
                    boolean isAttended = (attended != null && attended);

                    boolean success = bookingService.updateAttendance(bookingId, selectedMeal, isAttended);
                    if (success) {
                        updatedCount++;
                    }
                }

                JOptionPane.showMessageDialog(
                        AdminEntryFrame.this,
                        "Attendance updated successfully for " + updatedCount + " student(s) for " + selectedMeal + "!",
                        "Attendance Saved",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (SQLException ex) {
                showError("Error saving attendance to database: " + ex.getMessage());
            }
        }
    }
}
