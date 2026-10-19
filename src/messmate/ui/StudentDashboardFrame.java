package messmate.ui;

import messmate.model.Student;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Student Dashboard providing access to Book Meal, View Bookings, and Bill.
 * Demonstrates Syllabus:
 * - Module 1: Classes & Methods
 * - Module 2: Inheritance (BaseFrame), Polymorphic Student model
 * - Module 4: Swing Layout Managers (BorderLayout, GridLayout), Buttons, Event Handling
 */
public class StudentDashboardFrame extends BaseFrame {

    private Student currentStudent;

    public StudentDashboardFrame(Student student) {
        super("Messmate — Student Dashboard", 500, 480);
        this.currentStudent = student;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createHeaderPanel(
                "Welcome, " + currentStudent.getFullName(),
                "Room No: " + currentStudent.getRoomNumber() + " | Student Mess Portal"
        );
        add(headerPanel, BorderLayout.NORTH);

        // Center Action Buttons Panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BG_LIGHT);

        JPanel buttonCard = new JPanel();
        buttonCard.setLayout(new GridLayout(4, 1, 0, 15));
        buttonCard.setBackground(CARD_BG);
        buttonCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createEmptyBorder(25, 40, 25, 40)
        ));

        // 1. Book Meal Button
        JButton btnBookMeal = createPrimaryButton("Book a Meal");
        btnBookMeal.setPreferredSize(new Dimension(280, 45));
        btnBookMeal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BookMealFrame(currentStudent, StudentDashboardFrame.this).setVisible(true);
            }
        });
        buttonCard.add(btnBookMeal);

        // 2. View Bookings Button
        JButton btnViewBookings = createButton("View Bookings", SECONDARY_COLOR, Color.WHITE);
        btnViewBookings.setPreferredSize(new Dimension(280, 45));
        btnViewBookings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ViewBookingsFrame(currentStudent, StudentDashboardFrame.this).setVisible(true);
            }
        });
        buttonCard.add(btnViewBookings);

        // 3. Bill Button
        JButton btnBill = createAccentButton("Monthly Mess Bill");
        btnBill.setPreferredSize(new Dimension(280, 45));
        btnBill.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BillFrame(currentStudent, StudentDashboardFrame.this).setVisible(true);
            }
        });
        buttonCard.add(btnBill);

        // 4. Logout Button
        JButton btnLogout = createDangerButton("Sign Out");
        btnLogout.setPreferredSize(new Dimension(280, 45));
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        StudentDashboardFrame.this,
                        "Are you sure you want to log out?",
                        "Logout Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    new LoginFrame().setVisible(true);
                    dispose();
                }
            }
        });
        buttonCard.add(btnLogout);

        centerPanel.add(buttonCard);
        add(centerPanel, BorderLayout.CENTER);

        // Footer note
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG_LIGHT);
        JLabel lblNotice = new JLabel("Notice: Meal bookings must be submitted at least 2 days in advance.");
        lblNotice.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblNotice.setForeground(new Color(108, 117, 125));
        footer.add(lblNotice);
        add(footer, BorderLayout.SOUTH);
    }
}
