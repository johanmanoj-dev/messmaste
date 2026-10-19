package messmate.ui;

import messmate.model.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Admin Dashboard providing access to Bookings Overview and Meal Entry attendance.
 * Demonstrates Syllabus:
 * - Module 1: Classes & Methods
 * - Module 2: Inheritance (BaseFrame), Admin model
 * - Module 4: Swing Containers, Buttons, Event Delegation
 */
public class AdminDashboardFrame extends BaseFrame {

    private Admin currentAdmin;

    public AdminDashboardFrame(Admin admin) {
        super("Messmate — Admin Control Panel", 500, 440);
        this.currentAdmin = admin;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createHeaderPanel(
                "Mess Administration",
                "Logged in as: " + currentAdmin.getFullName() + " (ID: " + currentAdmin.getUsername() + ")"
        );
        add(headerPanel, BorderLayout.NORTH);

        // Center Action Buttons Panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BG_LIGHT);

        JPanel buttonCard = new JPanel();
        buttonCard.setLayout(new GridLayout(3, 1, 0, 16));
        buttonCard.setBackground(CARD_BG);
        buttonCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createEmptyBorder(25, 40, 25, 40)
        ));

        // 1. Daily Bookings Summary Button
        JButton btnBookings = createPrimaryButton("Daily Bookings Summary");
        btnBookings.setPreferredSize(new Dimension(280, 45));
        btnBookings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdminBookingsFrame(currentAdmin, AdminDashboardFrame.this).setVisible(true);
            }
        });
        buttonCard.add(btnBookings);

        // 2. Entry & Attendance Button
        JButton btnEntry = createButton("Meal Entry & Attendance", SECONDARY_COLOR, Color.WHITE);
        btnEntry.setPreferredSize(new Dimension(280, 45));
        btnEntry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdminEntryFrame(currentAdmin, AdminDashboardFrame.this).setVisible(true);
            }
        });
        buttonCard.add(btnEntry);

        // 3. Logout Button
        JButton btnLogout = createDangerButton("Sign Out");
        btnLogout.setPreferredSize(new Dimension(280, 45));
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        AdminDashboardFrame.this,
                        "Are you sure you want to sign out from administrator portal?",
                        "Confirm Logout",
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

        // Footer status
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG_LIGHT);
        JLabel lblNotice = new JLabel("Messmate Hostel Mess Management System v1.0");
        lblNotice.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblNotice.setForeground(Color.GRAY);
        footer.add(lblNotice);
        add(footer, BorderLayout.SOUTH);
    }
}
