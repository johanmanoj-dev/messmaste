package messmate.ui;

import messmate.exception.DuplicateBookingException;
import messmate.exception.InvalidBookingDateException;
import messmate.model.MealBooking;
import messmate.model.MealPricing;
import messmate.model.Student;
import messmate.service.BookingService;
import messmate.service.IBookingService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Screen for booking meals.
 * Simple, standard college-level Swing GUI.
 */
public class BookMealFrame extends BaseFrame {

    private Student currentStudent;
    private JFrame parentDashboard;
    private IBookingService bookingService;

    private JTextField txtMealDate;
    private JCheckBox chkBreakfast;
    private JCheckBox chkLunch;
    private JCheckBox chkSnacks;
    private JCheckBox chkDinner;
    private JButton btnConfirm;
    private JButton btnBack;

    public BookMealFrame(Student student, JFrame parent) {
        super("Messmate - Book Meal", 420, 420);
        this.currentStudent = student;
        this.parentDashboard = parent;
        this.bookingService = new BookingService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // 1. Top Title
        JLabel lblTitle = new JLabel("Book a Meal", JLabel.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(lblTitle, BorderLayout.NORTH);

        // 2. Center Panel (Form)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // Date Row
        JPanel dateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        dateRow.add(new JLabel("Meal Date (YYYY-MM-DD):"));
        txtMealDate = new JTextField(LocalDate.now().plusDays(2).toString(), 10);
        dateRow.add(txtMealDate);
        centerPanel.add(dateRow);

        JLabel lblRule = new JLabel("(Must be at least 2 days later than today)");
        lblRule.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblRule.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
        centerPanel.add(lblRule);

        // Meal Checkboxes
        centerPanel.add(new JLabel("Select Meals:"));
        chkBreakfast = new JCheckBox("Breakfast");
        chkLunch = new JCheckBox("Lunch");
        chkSnacks = new JCheckBox("Evening Snacks");
        chkDinner = new JCheckBox("Dinner");

        centerPanel.add(chkBreakfast);
        centerPanel.add(chkLunch);
        centerPanel.add(chkSnacks);
        centerPanel.add(chkDinner);

        add(centerPanel, BorderLayout.CENTER);

        // 3. Bottom Button Row
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnConfirm = new JButton("Confirm Booking");
        btnConfirm.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnConfirm.addActionListener(new ConfirmAction());
        bottomRow.add(btnConfirm);

        btnBack = new JButton("Cancel");
        btnBack.addActionListener(e -> {
            parentDashboard.setVisible(true);
            dispose();
        });
        bottomRow.add(btnBack);

        add(bottomRow, BorderLayout.SOUTH);
    }

    private class ConfirmAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String dateText = txtMealDate.getText().trim();
            if (dateText.isEmpty()) {
                showError("Please enter a meal date.");
                return;
            }

            LocalDate date;
            try {
                date = LocalDate.parse(dateText);
            } catch (DateTimeParseException ex) {
                showError("Invalid date format! Use YYYY-MM-DD.");
                return;
            }

            // Check advance rule
            long days = ChronoUnit.DAYS.between(LocalDate.now(), date);
            if (days < 2) {
                showError("Bookings must be at least 2 days in advance!\nEarliest date is: " + LocalDate.now().plusDays(2));
                return;
            }

            boolean b = chkBreakfast.isSelected();
            boolean l = chkLunch.isSelected();
            boolean s = chkSnacks.isSelected();
            boolean d = chkDinner.isSelected();

            if (!b && !l && !s && !d) {
                showError("Please select at least one meal.");
                return;
            }

            // Pop-up confirmation dialog
            int confirm = JOptionPane.showConfirmDialog(
                    BookMealFrame.this,
                    "Do you want to confirm this booking for " + date + "?",
                    "Confirm Booking",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (confirm != JOptionPane.OK_OPTION) {
                return; // User cancelled; stay on screen to edit
            }

            // Book meal
            try {
                MealBooking booking = new MealBooking(currentStudent.getId(), date, LocalDate.now(), b, l, s, d);
                boolean success = bookingService.bookMeal(booking);
                if (success) {
                    showInfo("Meal booked successfully for " + date + "!");
                    parentDashboard.setVisible(true);
                    dispose();
                } else {
                    showError("Booking failed. Please try again.");
                }
            } catch (InvalidBookingDateException | DuplicateBookingException ex) {
                showError(ex.getMessage());
            } catch (SQLException ex) {
                showError("Database error: " + ex.getMessage());
            }
        }
    }
}
