package messmate.ui;

import messmate.model.Admin;

import javax.swing.*;
import java.awt.*;

/**
 * Admin Dashboard providing 3 simple options.
 */
public class AdminDashboardFrame extends BaseFrame {

    private Admin currentAdmin;

    public AdminDashboardFrame(Admin admin) {
        super("Messmate - Admin Panel", 400, 320);
        this.currentAdmin = admin;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 15));

        // 1. Top Title
        JLabel lblTitle = new JLabel("Admin: " + currentAdmin.getFullName(), JLabel.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        add(lblTitle, BorderLayout.NORTH);

        // 2. Center Buttons
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 15));
        buttonPanel.setPreferredSize(new Dimension(240, 140));

        JButton btnBookings = new JButton("Daily Bookings Summary");
        btnBookings.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnBookings.addActionListener(e -> new AdminBookingsFrame(currentAdmin, this).setVisible(true));
        buttonPanel.add(btnBookings);

        JButton btnEntry = new JButton("Meal Entry & Attendance");
        btnEntry.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnEntry.addActionListener(e -> new AdminEntryFrame(currentAdmin, this).setVisible(true));
        buttonPanel.add(btnEntry);

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

        // 3. Bottom Note
        JLabel lblNote = new JLabel("Messmate Hostel Mess System", JLabel.CENTER);
        lblNote.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblNote.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(lblNote, BorderLayout.SOUTH);
    }
}
