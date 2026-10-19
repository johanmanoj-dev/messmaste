package messmate.ui;

import messmate.model.MealPricing;
import messmate.model.Student;
import messmate.service.BookingService;
import messmate.service.IBookingService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Screen displaying the real-time current month's bill counter
 * and past months' bill summary cards.
 * Demonstrates Syllabus:
 * - Module 1: Primitive types, arithmetic operators
 * - Module 2: Method Overloading (calls calculateBill(id) and calculateBill(id, month, year))
 * - Module 4: Swing UI hierarchy, borders, custom card layouts
 */
public class BillFrame extends BaseFrame {

    private Student currentStudent;
    private JFrame parentDashboard;
    private IBookingService bookingService;

    public BillFrame(Student student, JFrame parent) {
        super("Messmate — Mess Bill Summary", 560, 620);
        this.currentStudent = student;
        this.parentDashboard = parent;
        this.bookingService = new BookingService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createHeaderPanel(
                "Mess Bill Overview",
                "Student: " + currentStudent.getFullName() + " | Room No: " + currentStudent.getRoomNumber()
        );
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(BG_LIGHT);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // --- 1. Real-Time Current Month Counter Card ---
        LocalDate now = LocalDate.now();
        String currentMonthName = now.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        JPanel currentMonthCard = new JPanel(new BorderLayout(15, 10));
        currentMonthCard.setBackground(CARD_BG);
        currentMonthCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.setBackground(CARD_BG);

        JLabel lblCounterTitle = new JLabel("CURRENT MONTH: " + currentMonthName.toUpperCase() + " " + now.getYear());
        lblCounterTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblCounterTitle.setForeground(PRIMARY_COLOR);
        labelPanel.add(lblCounterTitle);

        JLabel lblCounterSub = new JLabel("Live automated total from confirmed bookings");
        lblCounterSub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblCounterSub.setForeground(Color.GRAY);
        labelPanel.add(lblCounterSub);

        currentMonthCard.add(labelPanel, BorderLayout.CENTER);

        // Fetch current month total (Method Overloading 1: calculateBill(studentId))
        double currentTotal = 0.0;
        try {
            currentTotal = bookingService.calculateBill(currentStudent.getId());
        } catch (SQLException ex) {
            showError("Could not retrieve current month bill: " + ex.getMessage());
        }

        JLabel lblCurrentAmount = new JLabel("₹ " + String.format("%.2f", currentTotal));
        lblCurrentAmount.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblCurrentAmount.setForeground(ACCENT_COLOR);
        currentMonthCard.add(lblCurrentAmount, BorderLayout.EAST);

        mainContent.add(currentMonthCard);
        mainContent.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- 2. Fixed Tariff Rates Strip ---
        JPanel tariffStrip = new JPanel(new GridLayout(1, 4, 8, 0));
        tariffStrip.setBackground(BG_LIGHT);
        tariffStrip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        tariffStrip.add(createTariffPill("Breakfast", "₹" + (int)MealPricing.BREAKFAST_PRICE));
        tariffStrip.add(createTariffPill("Lunch", "₹" + (int)MealPricing.LUNCH_PRICE));
        tariffStrip.add(createTariffPill("Snacks", "₹" + (int)MealPricing.SNACKS_PRICE));
        tariffStrip.add(createTariffPill("Dinner", "₹" + (int)MealPricing.DINNER_PRICE));

        mainContent.add(tariffStrip);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- 3. Past Months' Billing Cards ---
        JLabel lblPastTitle = new JLabel("Past Billing History");
        lblPastTitle.setFont(SUBTITLE_FONT);
        lblPastTitle.setForeground(TEXT_DARK);
        lblPastTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(lblPastTitle);
        mainContent.add(Box.createRigidArea(new Dimension(0, 10)));

        // Display previous 4 months
        boolean hasPast = false;
        for (int i = 1; i <= 4; i++) {
            LocalDate pastDate = now.minusMonths(i);
            int pastMonth = pastDate.getMonthValue();
            int pastYear = pastDate.getYear();

            try {
                // Method Overloading 2 (Module 2 Polymorphism): calculateBill(id, month, year)
                double pastBill = bookingService.calculateBill(currentStudent.getId(), pastMonth, pastYear);

                if (pastBill > 0.0) {
                    hasPast = true;
                    String pMonthName = pastDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                    mainContent.add(createPastMonthCard(pMonthName + " " + pastYear, pastBill));
                    mainContent.add(Box.createRigidArea(new Dimension(0, 8)));
                }
            } catch (SQLException ignored) {}
        }

        if (!hasPast) {
            JLabel lblNoPast = new JLabel("No previous billing history found.");
            lblNoPast.setFont(BODY_FONT);
            lblNoPast.setForeground(Color.GRAY);
            mainContent.add(lblNoPast);
        }

        JScrollPane scrollPane = new JScrollPane(mainContent);
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

    private JPanel createTariffPill(String name, String price) {
        JPanel pill = new JPanel(new BorderLayout());
        pill.setBackground(new Color(230, 238, 248));
        pill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 205, 230), 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        JLabel lblName = new JLabel(name, SwingConstants.CENTER);
        lblName.setFont(new Font("SansSerif", Font.PLAIN, 11));
        JLabel lblPrice = new JLabel(price, SwingConstants.CENTER);
        lblPrice.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblPrice.setForeground(PRIMARY_COLOR);

        pill.add(lblName, BorderLayout.NORTH);
        pill.add(lblPrice, BorderLayout.SOUTH);
        return pill;
    }

    private JPanel createPastMonthCard(String monthYear, double amount) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblTitle = new JLabel(monthYear);
        lblTitle.setFont(BODY_FONT);

        JLabel lblAmount = new JLabel("₹ " + String.format("%.2f", amount));
        lblAmount.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblAmount.setForeground(PRIMARY_COLOR);

        card.add(lblTitle, BorderLayout.WEST);
        card.add(lblAmount, BorderLayout.EAST);
        return card;
    }
}
