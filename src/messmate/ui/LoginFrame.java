package messmate.ui;

import messmate.db.DBConnection;
import messmate.db.DatabaseInitializer;
import messmate.model.Admin;
import messmate.model.Student;
import messmate.model.User;
import messmate.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Login Screen for Messmate.
 * Demonstrates Syllabus:
 * - Module 1: Control statements, variable scopes
 * - Module 2: Inner classes (LoginActionHandler), Dynamic Method Dispatch (User -> Student / Admin)
 * - Module 4: Swing Controls (JTextField, JPasswordField, JButton, JLabel), Delegation Event Model
 */
public class LoginFrame extends BaseFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnDbConfig;
    private UserService userService;

    public LoginFrame() {
        super("Messmate — Login Portal", 450, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.userService = new UserService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createHeaderPanel("Messmate", "Campus Hostel Mess Booking System");
        add(headerPanel, BorderLayout.NORTH);

        // Center Login Card
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(BG_LIGHT);

        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(CARD_BG);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        JLabel lblLoginTitle = new JLabel("Account Login");
        lblLoginTitle.setFont(SUBTITLE_FONT);
        lblLoginTitle.setForeground(PRIMARY_COLOR);
        lblLoginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblLoginTitle);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Username Field
        JLabel lblUser = new JLabel("Username (Room No. / Admin ID):");
        lblUser.setFont(BODY_FONT);
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(lblUser);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 4)));

        txtUsername = new JTextField(15);
        txtUsername.setFont(BODY_FONT);
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cardPanel.add(txtUsername);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Password Field
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(BODY_FONT);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(lblPass);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 4)));

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(BODY_FONT);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cardPanel.add(txtPassword);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Login Button
        btnLogin = createPrimaryButton("Sign In");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // Register Event Listener using Inner Class (Module 2 & 4)
        btnLogin.addActionListener(new LoginActionHandler());
        cardPanel.add(btnLogin);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Predefined account hints
        JLabel lblHint = new JLabel("<html><center style='color:#6c757d; font-size:10px;'>"
                + "Default Logins:<br>"
                + "Student: Room <b>101</b> | Pass: <b>pass101</b><br>"
                + "Admin: User <b>admin</b> | Pass: <b>admin123</b>"
                + "</center></html>");
        lblHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblHint);

        centerWrapper.add(cardPanel);
        add(centerWrapper, BorderLayout.CENTER);

        // Bottom Footer with DB Settings Button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnDbConfig = createButton("MySQL Settings", new Color(225, 230, 238), PRIMARY_COLOR);
        btnDbConfig.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnDbConfig.addActionListener(e -> showDbConfigDialog());
        footerPanel.add(btnDbConfig);
        add(footerPanel, BorderLayout.SOUTH);

        // Allow pressing Enter in password field to submit
        txtPassword.addActionListener(new LoginActionHandler());
    }

    /**
     * Inner Class implementing ActionListener.
     * Demonstrates Syllabus Module 2 (Inner Classes) and Module 4 (Event Handling Delegation Model).
     */
    private class LoginActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                showError("Please enter both username and password.");
                return;
            }

            try {
                // Attempt login via service
                User user = userService.login(username, password);

                if (user == null) {
                    showError("Invalid username or password. Please try again.");
                    return;
                }

                // Dynamic Method Dispatch (Module 2 Polymorphism)
                System.out.println("Login Success: " + user.displayInfo());

                // Route based on role
                if (user instanceof Student) {
                    Student student = (Student) user;
                    new StudentDashboardFrame(student).setVisible(true);
                    dispose();
                } else if (user instanceof Admin) {
                    Admin admin = (Admin) user;
                    new AdminDashboardFrame(admin).setVisible(true);
                    dispose();
                } else {
                    showError("Unknown user role: " + user.getRole());
                }

            } catch (SQLException ex) {
                // Check if connection refused or database down
                showError("Database Error: " + ex.getMessage()
                        + "\n\nPlease ensure MySQL server is running in XAMPP / MySQL Service."
                        + "\nYou can check connection settings via 'MySQL Settings' below.");
            }
        }
    }

    /**
     * Dialog to configure MySQL host, port, user, password.
     */
    private void showDbConfigDialog() {
        JTextField hostField = new JTextField(DBConnection.getHost(), 15);
        JTextField portField = new JTextField(String.valueOf(DBConnection.getPort()), 6);
        JTextField dbField = new JTextField(DBConnection.getDbName(), 15);
        JTextField userField = new JTextField(DBConnection.getUsername(), 15);
        JPasswordField passField = new JPasswordField(DBConnection.getPassword(), 15);

        JPanel panel = new JPanel(new GridLayout(5, 2, 8, 8));
        panel.add(new JLabel("Host:"));
        panel.add(hostField);
        panel.add(new JLabel("Port:"));
        panel.add(portField);
        panel.add(new JLabel("Database:"));
        panel.add(dbField);
        panel.add(new JLabel("User:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "MySQL Database Settings",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                int port = Integer.parseInt(portField.getText().trim());
                DBConnection.saveProperties(
                        hostField.getText().trim(),
                        port,
                        dbField.getText().trim(),
                        userField.getText().trim(),
                        new String(passField.getPassword()).trim()
                );
                // Initialize schema & seeds on new connection
                DatabaseInitializer.initialize();
                showInfo("Database connection saved and initialized successfully!");
            } catch (Exception ex) {
                showError("Failed to initialize database with new settings: " + ex.getMessage());
            }
        }
    }
}
