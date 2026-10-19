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
 * Simple, standard college-level Swing GUI.
 */
public class LoginFrame extends BaseFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnDbConfig;
    private UserService userService;

    public LoginFrame() {
        super("Messmate - Login", 400, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.userService = new UserService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 15));

        // 1. Top Title
        JLabel lblTitle = new JLabel("Messmate - Login", JLabel.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(lblTitle, BorderLayout.NORTH);

        // 2. Center Form Panel (Simple GridLayout)
        JPanel formWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 12));

        formPanel.add(new JLabel("Username:"));
        txtUsername = new JTextField(12);
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField(12);
        formPanel.add(txtPassword);

        formPanel.add(new JLabel("")); // Empty cell for spacing
        btnLogin = new JButton("Sign In");
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLogin.addActionListener(new LoginActionHandler());
        formPanel.add(btnLogin);

        formWrapper.add(formPanel);
        add(formWrapper, BorderLayout.CENTER);

        // 3. Bottom Panel (Hint & Database Settings)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JLabel lblHint = new JLabel("Default: Student '101' (pass101) | Admin 'admin' (admin123)", JLabel.CENTER);
        lblHint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        bottomPanel.add(lblHint, BorderLayout.CENTER);

        btnDbConfig = new JButton("MySQL Settings");
        btnDbConfig.setFont(new Font("SansSerif", Font.PLAIN, 10));
        btnDbConfig.addActionListener(e -> showDbConfigDialog());
        bottomPanel.add(btnDbConfig, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // Allow Enter key to log in
        txtPassword.addActionListener(new LoginActionHandler());
    }

    /**
     * Inner Class for Login Event Handling (Module 2 & 4)
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
                User user = userService.login(username, password);

                if (user == null) {
                    showError("Invalid username or password.");
                    return;
                }

                // Dynamic Method Dispatch / Polymorphism (Module 2)
                System.out.println("Logged in: " + user.displayInfo());

                if (user instanceof Student) {
                    new StudentDashboardFrame((Student) user).setVisible(true);
                    dispose();
                } else if (user instanceof Admin) {
                    new AdminDashboardFrame((Admin) user).setVisible(true);
                    dispose();
                }

            } catch (SQLException ex) {
                showError("Database Error: " + ex.getMessage()
                        + "\nEnsure MySQL is running on localhost:3306 or click 'MySQL Settings'.");
            }
        }
    }

    private void showDbConfigDialog() {
        JTextField hostField = new JTextField(DBConnection.getHost(), 10);
        JTextField portField = new JTextField(String.valueOf(DBConnection.getPort()), 5);
        JTextField dbField = new JTextField(DBConnection.getDbName(), 10);
        JTextField userField = new JTextField(DBConnection.getUsername(), 10);
        JPasswordField passField = new JPasswordField(DBConnection.getPassword(), 10);

        JPanel panel = new JPanel(new GridLayout(5, 2, 6, 6));
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
                this, panel, "MySQL Settings",
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
                DatabaseInitializer.initialize();
                showInfo("MySQL settings updated and connected successfully!");
            } catch (Exception ex) {
                showError("Failed to connect: " + ex.getMessage());
            }
        }
    }
}
