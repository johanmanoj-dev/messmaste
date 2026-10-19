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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Screen for marking student dining attendance.
 * Simple, standard college-level Swing GUI.
 */
public class AdminEntryFrame extends BaseFrame {

    private Admin currentAdmin;
    private JFrame parentDashboard;
    private IBookingService bookingService;

    private JTextField txtDate;
    private JComboBox<String> cmbMeal;
    private JTable tblStudents;
    private DefaultTableModel tableModel;
    private List<MealBooking> loadedBookings = new ArrayList<>();

    public AdminEntryFrame(Admin admin, JFrame parent) {
        super("Messmate - Meal Entry & Attendance", 600, 450);
        this.currentAdmin = admin;
        this.parentDashboard = parent;
        this.bookingService = new BookingService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // 1. Top Filter Row (Date + Meal Dropdown + Load Button)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        topPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        txtDate = new JTextField(LocalDate.now().plusDays(2).toString(), 10);
        topPanel.add(txtDate);

        topPanel.add(new JLabel("Meal:"));
        String[] meals = {"Breakfast", "Lunch", "Evening Snacks", "Dinner"};
        cmbMeal = new JComboBox<>(meals);
        topPanel.add(cmbMeal);

        JButton btnLoad = new JButton("Load Students");
        btnLoad.addActionListener(e -> loadStudents());
        topPanel.add(btnLoad);

        add(topPanel, BorderLayout.NORTH);

        // 2. Center Table (Interactive Checkboxes for Attendance)
        Vector<String> columns = new Vector<>();
        columns.add("Room No");
        columns.add("Student Name");
        columns.add("Attended (Mark)");

        tableModel = new DefaultTableModel(new Vector<Vector<Object>>(), columns) {
            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 2) return Boolean.class; // Checkbox column
                return Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 2; // only checkbox is editable
            }
        };

        tblStudents = new JTable(tableModel);
        tblStudents.setRowHeight(24);
        add(new JScrollPane(tblStudents), BorderLayout.CENTER);

        // 3. Bottom Button Row
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnSave = new JButton("Save Attendance");
        btnSave.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSave.addActionListener(e -> saveAttendance());
        bottomPanel.add(btnSave);

        JButton btnBack = new JButton("Back to Dashboard");
        btnBack.addActionListener(e -> {
            parentDashboard.setVisible(true);
            dispose();
        });
        bottomPanel.add(btnBack);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadStudents() {
        String dateStr = txtDate.getText().trim();
        String selectedMeal = (String) cmbMeal.getSelectedItem();

        try {
            LocalDate date = LocalDate.parse(dateStr);
            loadedBookings = bookingService.getBookingsByDateAndMeal(date, selectedMeal);
            tableModel.setRowCount(0);

            if (loadedBookings.isEmpty()) {
                showInfo("No students booked for " + selectedMeal + " on " + date + ".");
                return;
            }

            for (MealBooking b : loadedBookings) {
                Vector<Object> row = new Vector<>();
                row.add(b.getRoomNumber() != null ? b.getRoomNumber() : "-");
                row.add(b.getStudentName() != null ? b.getStudentName() : "-");

                boolean attended = false;
                if ("Breakfast".equalsIgnoreCase(selectedMeal)) attended = b.isBreakfastAttended();
                else if ("Lunch".equalsIgnoreCase(selectedMeal)) attended = b.isLunchAttended();
                else if ("Evening Snacks".equalsIgnoreCase(selectedMeal)) attended = b.isSnacksAttended();
                else if ("Dinner".equalsIgnoreCase(selectedMeal)) attended = b.isDinnerAttended();

                row.add(Boolean.valueOf(attended));
                tableModel.addRow(row);
            }

        } catch (DateTimeParseException ex) {
            showError("Invalid date format! Use YYYY-MM-DD.");
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }

    private void saveAttendance() {
        if (loadedBookings.isEmpty() || tableModel.getRowCount() == 0) {
            showError("No student records loaded.");
            return;
        }

        String selectedMeal = (String) cmbMeal.getSelectedItem();
        int count = 0;

        try {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int bookingId = loadedBookings.get(i).getId();
                Boolean attended = (Boolean) tableModel.getValueAt(i, 2);
                boolean isAttended = (attended != null && attended);

                if (bookingService.updateAttendance(bookingId, selectedMeal, isAttended)) {
                    count++;
                }
            }

            showInfo("Attendance updated successfully for " + count + " student(s)!");

        } catch (SQLException ex) {
            showError("Error saving attendance: " + ex.getMessage());
        }
    }
}
