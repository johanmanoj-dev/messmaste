package messmate.ui;

import messmate.model.MealBooking;
import messmate.model.Student;
import messmate.service.BookingService;
import messmate.service.IBookingService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * Screen displaying the student's booking history in chronological cards.
 * Demonstrates Syllabus:
 * - Module 1: Iteration Statements (for-each loop iterating booking cards), Encapsulation
 * - Module 2: Inheritance (BaseFrame), Polymorphism
 * - Module 4: Swing Components (JPanel, JScrollPane, JButton, JLabel, JOptionPane)
 */
public class ViewBookingsFrame extends BaseFrame {

    private Student currentStudent;
    private JFrame parentDashboard;
    private IBookingService bookingService;

    public ViewBookingsFrame(Student student, JFrame parent) {
        super("Messmate — My Bookings History", 580, 620);
        this.currentStudent = student;
        this.parentDashboard = parent;
        this.bookingService = new BookingService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createHeaderPanel(
                "My Meal Bookings",
                "Chronological list of all active and completed bookings"
        );
        add(headerPanel, BorderLayout.NORTH);

        // Center scrollable card list container
        JPanel cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(BG_LIGHT);
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        try {
            List<MealBooking> bookings = bookingService.getBookingsByStudent(currentStudent.getId());

            if (bookings.isEmpty()) {
                JPanel emptyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                emptyPanel.setBackground(BG_LIGHT);
                JLabel lblEmpty = new JLabel("No meal bookings found. Click 'Book Meal' on your dashboard to get started!");
                lblEmpty.setFont(BODY_FONT);
                lblEmpty.setForeground(Color.GRAY);
                emptyPanel.add(lblEmpty);
                cardsContainer.add(emptyPanel);
            } else {
                for (MealBooking b : bookings) {
                    cardsContainer.add(createBookingCard(b));
                    cardsContainer.add(Box.createRigidArea(new Dimension(0, 12)));
                }
            }

        } catch (SQLException ex) {
            showError("Failed to load bookings: " + ex.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Bar
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
     * Builds an individual visual Card component for a booking.
     */
    private JPanel createBookingCard(MealBooking booking) {
        JPanel card = new JPanel(new BorderLayout(10, 8));
        card.setBackground(CARD_BG);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 220, 228), 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        // Left Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(CARD_BG);

        JLabel lblMealDate = new JLabel("Meal Date: " + booking.getMealDate());
        lblMealDate.setFont(SUBTITLE_FONT);
        lblMealDate.setForeground(PRIMARY_COLOR);
        infoPanel.add(lblMealDate);

        JLabel lblBookedOn = new JLabel("Booked on: " + booking.getBookedAt()
                + "  |  Total: ₹" + String.format("%.2f", booking.getTotalCost()));
        lblBookedOn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblBookedOn.setForeground(new Color(100, 110, 120));
        infoPanel.add(lblBookedOn);

        card.add(infoPanel, BorderLayout.CENTER);

        // Right Action: Status & View Details Button
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionPanel.setBackground(CARD_BG);

        // Status badge
        String status = booking.getStatus();
        JLabel lblStatus = new JLabel(status);
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
        if ("Active".equalsIgnoreCase(status)) {
            lblStatus.setForeground(new Color(40, 167, 69)); // Green
        } else {
            lblStatus.setForeground(new Color(108, 117, 125)); // Muted Gray
        }
        actionPanel.add(lblStatus);

        JButton btnDetails = createPrimaryButton("View Details");
        btnDetails.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBookingDetailsModal(booking);
            }
        });
        actionPanel.add(btnDetails);

        card.add(actionPanel, BorderLayout.EAST);
        return card;
    }

    /**
     * Shows a popup dialog with the breakdown of the selected booking.
     */
    private void showBookingDetailsModal(MealBooking b) {
        String detailsHtml = "<html><body style='font-family:sans-serif; width: 280px;'>"
                + "<h3 style='color:#184C78; margin-bottom:5px;'>Booking Breakdown</h3>"
                + "<b>Target Meal Date:</b> " + b.getMealDate() + "<br>"
                + "<b>Booked At:</b> " + b.getBookedAt() + "<br>"
                + "<b>Current Status:</b> " + b.getStatus() + "<br>"
                + "<hr>"
                + "<b>Meals Reserved:</b><br>"
                + "• Breakfast: " + (b.isBreakfast() ? "<b>YES</b> (₹30)" : "No")
                + (b.isBreakfast() ? (b.isBreakfastAttended() ? " <font color='green'>[Attended]</font>" : " [Not Attended]") : "") + "<br>"
                + "• Lunch: " + (b.isLunch() ? "<b>YES</b> (₹50)" : "No")
                + (b.isLunch() ? (b.isLunchAttended() ? " <font color='green'>[Attended]</font>" : " [Not Attended]") : "") + "<br>"
                + "• Evening Snacks: " + (b.isSnacks() ? "<b>YES</b> (₹20)" : "No")
                + (b.isSnacks() ? (b.isSnacksAttended() ? " <font color='green'>[Attended]</font>" : " [Not Attended]") : "") + "<br>"
                + "• Dinner: " + (b.isDinner() ? "<b>YES</b> (₹50)" : "No")
                + (b.isDinner() ? (b.isDinnerAttended() ? " <font color='green'>[Attended]</font>" : " [Not Attended]") : "") + "<br>"
                + "<hr>"
                + "<b>Total Booking Amount:</b> <font color='#184C78' size='+1'>₹" + String.format("%.2f", b.getTotalCost()) + "</font>"
                + "</body></html>";

        JOptionPane.showMessageDialog(
                this,
                detailsHtml,
                "Booking Details — " + b.getMealDate(),
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
