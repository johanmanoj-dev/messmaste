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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Screen for booking meals with 2-day advance validation and confirmation.
 * Demonstrates Syllabus:
 * - Module 1: Control statements, conditional logic, arithmetic operators
 * - Module 2: Inner classes (ItemListener, ActionListener)
 * - Module 3: Interfaces (IBookingService), Custom Exceptions (InvalidBookingDateException, DuplicateBookingException)
 * - Module 4: Swing controls (JTextField, JCheckBox, JButton, JOptionPane), Event Handling
 */
public class BookMealFrame extends BaseFrame {

    private Student currentStudent;
    private JFrame parentDashboard;
    private IBookingService bookingService;

    // UI Components
    private JTextField txtMealDate;
    private JButton btnCheckDate;
    private JPanel mealSelectionPanel;
    private JCheckBox chkBreakfast;
    private JCheckBox chkLunch;
    private JCheckBox chkSnacks;
    private JCheckBox chkDinner;
    private JLabel lblTotalCost;
    private JButton btnConfirmBooking;

    private LocalDate validatedDate = null;

    public BookMealFrame(Student student, JFrame parent) {
        super("Messmate — Book a Meal", 520, 560);
        this.currentStudent = student;
        this.parentDashboard = parent;
        // Interface reference to concrete service (Module 3 Interface polymorphism)
        this.bookingService = new BookingService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createHeaderPanel(
                "Book a Meal",
                "Select meal date (min. 2 days ahead) and choose desired meals"
        );
        add(headerPanel, BorderLayout.NORTH);

        // Main content container
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_LIGHT);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // --- Step 1: Date Selection Box ---
        JPanel dateCard = new JPanel(new BorderLayout(10, 10));
        dateCard.setBackground(CARD_BG);
        dateCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 205, 215), 1),
                        "Step 1: Select Meal Date (YYYY-MM-DD)"
                ),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        // Suggest earliest valid date (today + 2 days)
        LocalDate defaultDate = LocalDate.now().plusDays(2);
        txtMealDate = new JTextField(defaultDate.toString(), 12);
        txtMealDate.setFont(BODY_FONT);

        btnCheckDate = createPrimaryButton("Verify Date");
        btnCheckDate.addActionListener(new VerifyDateListener());

        JPanel dateInputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        dateInputRow.setBackground(CARD_BG);
        dateInputRow.add(new JLabel("Meal Date:"));
        dateInputRow.add(txtMealDate);
        dateInputRow.add(btnCheckDate);

        JLabel lblDateRule = new JLabel("Rule: Date must be at least 2 days later than today.");
        lblDateRule.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblDateRule.setForeground(new Color(108, 117, 125));

        dateCard.add(dateInputRow, BorderLayout.CENTER);
        dateCard.add(lblDateRule, BorderLayout.SOUTH);
        contentPanel.add(dateCard);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- Step 2: Meal Selection Box (Initially Hidden until date is validated) ---
        mealSelectionPanel = new JPanel();
        mealSelectionPanel.setLayout(new BoxLayout(mealSelectionPanel, BoxLayout.Y_AXIS));
        mealSelectionPanel.setBackground(CARD_BG);
        mealSelectionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 205, 215), 1),
                        "Step 2: Select Meals"
                ),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        chkBreakfast = new JCheckBox("Breakfast (₹" + (int)MealPricing.BREAKFAST_PRICE + ")");
        chkLunch = new JCheckBox("Lunch (₹" + (int)MealPricing.LUNCH_PRICE + ")");
        chkSnacks = new JCheckBox("Evening Snacks (₹" + (int)MealPricing.SNACKS_PRICE + ")");
        chkDinner = new JCheckBox("Dinner (₹" + (int)MealPricing.DINNER_PRICE + ")");

        Font chkFont = new Font("SansSerif", Font.PLAIN, 14);
        chkBreakfast.setFont(chkFont);
        chkLunch.setFont(chkFont);
        chkSnacks.setFont(chkFont);
        chkDinner.setFont(chkFont);

        chkBreakfast.setBackground(CARD_BG);
        chkLunch.setBackground(CARD_BG);
        chkSnacks.setBackground(CARD_BG);
        chkDinner.setBackground(CARD_BG);

        // Dynamic price update listener (ItemListener)
        ItemListener costUpdater = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateCostDisplay();
            }
        };

        chkBreakfast.addItemListener(costUpdater);
        chkLunch.addItemListener(costUpdater);
        chkSnacks.addItemListener(costUpdater);
        chkDinner.addItemListener(costUpdater);

        mealSelectionPanel.add(chkBreakfast);
        mealSelectionPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        mealSelectionPanel.add(chkLunch);
        mealSelectionPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        mealSelectionPanel.add(chkSnacks);
        mealSelectionPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        mealSelectionPanel.add(chkDinner);
        mealSelectionPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        lblTotalCost = new JLabel("Total Estimated Cost: ₹0.00");
        lblTotalCost.setFont(SUBTITLE_FONT);
        lblTotalCost.setForeground(PRIMARY_COLOR);
        mealSelectionPanel.add(lblTotalCost);
        mealSelectionPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        btnConfirmBooking = createAccentButton("Confirm & Book Meals");
        btnConfirmBooking.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnConfirmBooking.addActionListener(new ConfirmBookingListener());
        mealSelectionPanel.add(btnConfirmBooking);

        // Initially disabled/invisible until date is verified
        mealSelectionPanel.setVisible(false);
        contentPanel.add(mealSelectionPanel);

        add(new JScrollPane(contentPanel), BorderLayout.CENTER);

        // Bottom Bar with Cancel/Back Button
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setBackground(BG_LIGHT);
        JButton btnBack = createButton("Back to Dashboard", new Color(120, 130, 140), Color.WHITE);
        btnBack.addActionListener(e -> {
            parentDashboard.setVisible(true);
            dispose();
        });
        bottomBar.add(btnBack);
        add(bottomBar, BorderLayout.SOUTH);
    }

    /**
     * Inner Class for verifying date requirements.
     * Demonstrates Module 3 Exception Handling & Module 4 Event Delegation.
     */
    private class VerifyDateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String dateText = txtMealDate.getText().trim();
            if (dateText.isEmpty()) {
                showError("Please enter a date.");
                return;
            }

            try {
                LocalDate date = LocalDate.parse(dateText);
                LocalDate today = LocalDate.now();

                // Validation Rule: at least 2 days later than today
                long days = ChronoUnit.DAYS.between(today, date);
                if (days < 2) {
                    throw new InvalidBookingDateException(
                            "Invalid Date! Meal bookings must be at least 2 days in advance.\n"
                            + "Earliest permissible booking date is: " + today.plusDays(2)
                    );
                }

                // Check if already booked
                if (bookingService.isDateAlreadyBooked(currentStudent.getId(), date)) {
                    throw new DuplicateBookingException(
                            "You already have a meal booking registered for " + date + "!\n"
                            + "Please check 'View Bookings' to inspect your existing reservation."
                    );
                }

                // Valid date accepted
                validatedDate = date;
                mealSelectionPanel.setVisible(true);
                txtMealDate.setEditable(false);
                btnCheckDate.setEnabled(false);
                showInfo("Date " + date + " is available! Please select your meals below.");
                revalidate();
                repaint();

            } catch (DateTimeParseException ex) {
                showError("Invalid date format! Please use YYYY-MM-DD (e.g. " + LocalDate.now().plusDays(2) + ").");
            } catch (InvalidBookingDateException | DuplicateBookingException ex) {
                // Demonstrates Module 3: Catching custom exceptions
                showError(ex.getMessage());
            } catch (SQLException ex) {
                showError("Database communication error: " + ex.getMessage());
            }
        }
    }

    /**
     * Updates the cost calculation display based on selected checkboxes.
     */
    private void updateCostDisplay() {
        double cost = MealPricing.calculateCost(
                chkBreakfast.isSelected(),
                chkLunch.isSelected(),
                chkSnacks.isSelected(),
                chkDinner.isSelected()
        );
        lblTotalCost.setText("Total Estimated Cost: ₹" + String.format("%.2f", cost));
    }

    /**
     * Inner Class for confirming and finalizing the booking.
     */
    private class ConfirmBookingListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (validatedDate == null) {
                showError("Please verify a valid date first.");
                return;
            }

            boolean b = chkBreakfast.isSelected();
            boolean l = chkLunch.isSelected();
            boolean s = chkSnacks.isSelected();
            boolean d = chkDinner.isSelected();

            if (!b && !l && !s && !d) {
                showError("Please tick at least one meal to book.");
                return;
            }

            double totalCost = MealPricing.calculateCost(b, l, s, d);

            // Pop-up confirmation dialog as specified in requirements
            int choice = JOptionPane.showConfirmDialog(
                    BookMealFrame.this,
                    "Do you want to confirm this meal booking for " + validatedDate + "?\n"
                    + "Selected Meals: "
                    + (b ? "[Breakfast] " : "")
                    + (l ? "[Lunch] " : "")
                    + (s ? "[Snacks] " : "")
                    + (d ? "[Dinner] " : "")
                    + "\nTotal Amount: ₹" + String.format("%.2f", totalCost),
                    "Confirm Booking",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            // If user clicks Cancel, they return to the meal selection screen to edit
            if (choice != JOptionPane.OK_OPTION) {
                return;
            }

            // If user clicked Confirm:
            try {
                MealBooking booking = new MealBooking(
                        currentStudent.getId(),
                        validatedDate,
                        LocalDate.now(),
                        b, l, s, d
                );

                boolean success = bookingService.bookMeal(booking);
                if (success) {
                    JOptionPane.showMessageDialog(
                            BookMealFrame.this,
                            "Meals booked successfully for " + validatedDate + "!\n"
                            + "Amount ₹" + String.format("%.2f", totalCost) + " added to your monthly bill.",
                            "Booking Confirmed",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    // Redirect back to Student Dashboard
                    parentDashboard.setVisible(true);
                    dispose();
                } else {
                    showError("Failed to book meals. Please try again.");
                }

            } catch (InvalidBookingDateException | DuplicateBookingException ex) {
                showError(ex.getMessage());
            } catch (SQLException ex) {
                showError("Database error during booking: " + ex.getMessage());
            }
        }
    }
}
