package messmate.ui;

import messmate.model.Student;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Student Dashboard providing 4 simple navigation buttons.
 */
public class StudentDashboardFrame extends BaseFrame {

    private Student currentStudent;

    public StudentDashboardFrame(Student student) {
        super("Messmate - Student Dashboard", 420, 360);
        this.currentStudent = student;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 15));

        // 1. Top Header Label
        JLabel lblWelcome = new JLabel(
                "Welcome, " + currentStudent.getFullName() + " (ID: " + currentStudent.getIdNumber() + ")",
                JLabel.CENTER
        );
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        add(lblWelcome, BorderLayout.NORTH);

        // 2. Center Buttons (Simple GridLayout)
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 12));
        buttonPanel.setPreferredSize(new Dimension(240, 180));

        JButton btnBookMeal = new JButton("Book a Meal");
        btnBookMeal.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnBookMeal.addActionListener(e -> new BookMealFrame(currentStudent, this).setVisible(true));
        buttonPanel.add(btnBookMeal);

        JButton btnViewBookings = new JButton("View Bookings");
        btnViewBookings.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnViewBookings.addActionListener(e -> new ViewBookingsFrame(currentStudent, this).setVisible(true));
        buttonPanel.add(btnViewBookings);

        JButton btnBill = new JButton("Monthly Bill");
        btnBill.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnBill.addActionListener(e -> new BillFrame(currentStudent, this).setVisible(true));
        buttonPanel.add(btnBill);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this, "Do you want to log out?", "Logout", JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        buttonPanel.add(btnLogout);

        centerWrapper.add(buttonPanel);
        add(centerWrapper, BorderLayout.CENTER);

        // 3. Bottom Notice
        JLabel lblNotice = new JLabel("Notice: Bookings must be made at least 2 days ahead.", JLabel.CENTER);
        lblNotice.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblNotice.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(lblNotice, BorderLayout.SOUTH);
    }
}
