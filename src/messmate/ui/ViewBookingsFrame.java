package messmate.ui;

import messmate.model.MealBooking;
import messmate.model.Student;
import messmate.service.BookingService;
import messmate.service.IBookingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

/**
 * Screen displaying the student's booking history.
 * Simple, standard college-level Swing JTable view.
 */
public class ViewBookingsFrame extends BaseFrame {

    private Student currentStudent;
    private JFrame parentDashboard;
    private IBookingService bookingService;
    private JTable tblBookings;
    private DefaultTableModel tableModel;
    private List<MealBooking> bookingList;

    public ViewBookingsFrame(Student student, JFrame parent) {
        super("Messmate - My Bookings", 550, 400);
        this.currentStudent = student;
        this.parentDashboard = parent;
        this.bookingService = new BookingService();
        initUI();
        loadBookings();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // 1. Top Title
        JLabel lblTitle = new JLabel("My Meal Bookings", JLabel.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(12, 0, 5, 0));
        add(lblTitle, BorderLayout.NORTH);

        // 2. Center Table using Vector (Module 1 Vector & Module 4 JTable)
        Vector<String> columns = new Vector<>();
        columns.add("Meal Date");
        columns.add("Booked On");
        columns.add("Total Cost");
        columns.add("Status");

        tableModel = new DefaultTableModel(new Vector<Vector<Object>>(), columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read only
            }
        };

        tblBookings = new JTable(tableModel);
        tblBookings.setRowHeight(22);
        tblBookings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tblBookings), BorderLayout.CENTER);

        // 3. Bottom Button Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnDetails = new JButton("View Details");
        btnDetails.addActionListener(e -> showSelectedDetails());
        bottomPanel.add(btnDetails);

        JButton btnBack = new JButton("Back to Dashboard");
        btnBack.addActionListener(e -> {
            parentDashboard.setVisible(true);
            dispose();
        });
        bottomPanel.add(btnBack);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadBookings() {
        try {
            bookingList = bookingService.getBookingsByStudent(currentStudent.getId());
            tableModel.setRowCount(0);

            for (MealBooking b : bookingList) {
                Vector<Object> row = new Vector<>();
                row.add(b.getMealDate().toString());
                row.add(b.getBookedAt().toString());
                row.add("₹ " + String.format("%.2f", b.getTotalCost()));
                row.add(b.getStatus()); // "Active" or "Completed"
                tableModel.addRow(row);
            }

            if (bookingList.isEmpty()) {
                showInfo("No bookings found. You can book meals from the dashboard.");
            }

        } catch (SQLException ex) {
            showError("Failed to load bookings: " + ex.getMessage());
        }
    }

    private void showSelectedDetails() {
        int selectedRow = tblBookings.getSelectedRow();
        if (selectedRow < 0) {
            showError("Please select a booking from the table first.");
            return;
        }

        MealBooking b = bookingList.get(selectedRow);

        String message = "Booking Details:\n\n"
                + "Meal Date: " + b.getMealDate() + "\n"
                + "Booked On: " + b.getBookedAt() + "\n"
                + "Status: " + b.getStatus() + "\n\n"
                + "Meals Selected:\n"
                + " - Breakfast: " + (b.isBreakfast() ? "Yes" + (b.isBreakfastAttended() ? " [Attended]" : "") : "No") + "\n"
                + " - Lunch: " + (b.isLunch() ? "Yes" + (b.isLunchAttended() ? " [Attended]" : "") : "No") + "\n"
                + " - Evening Snacks: " + (b.isSnacks() ? "Yes" + (b.isSnacksAttended() ? " [Attended]" : "") : "No") + "\n"
                + " - Dinner: " + (b.isDinner() ? "Yes" + (b.isDinnerAttended() ? " [Attended]" : "") : "No") + "\n\n"
                + "Total Amount: ₹ " + String.format("%.2f", b.getTotalCost());

        JOptionPane.showMessageDialog(this, message, "Booking Details", JOptionPane.INFORMATION_MESSAGE);
    }
}
