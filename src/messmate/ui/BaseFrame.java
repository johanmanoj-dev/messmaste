package messmate.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Base frame class providing common UI styling and window configuration.
 * Demonstrates Syllabus:
 * - Module 2: Inheritance (Superclass BaseFrame extended by all UI window frames)
 * - Module 4: Swing Containers (JFrame, JPanel) and Components
 */
public abstract class BaseFrame extends JFrame {

    // Modern clean color palette
    public static final Color PRIMARY_COLOR = new Color(24, 76, 120);     // Navy Blue
    public static final Color SECONDARY_COLOR = new Color(41, 128, 185); // Light Blue
    public static final Color ACCENT_COLOR = new Color(46, 139, 87);      // Sea Green
    public static final Color BG_LIGHT = new Color(245, 247, 250);       // Soft Gray Background
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_DARK = new Color(33, 37, 41);
    public static final Color DANGER_COLOR = new Color(192, 57, 43);

    // Standard Fonts
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 20);
    public static final Font SUBTITLE_FONT = new Font("SansSerif", Font.BOLD, 15);
    public static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 13);

    public BaseFrame(String title, int width, int height) {
        super(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        getContentPane().setBackground(BG_LIGHT);
    }

    /**
     * Helper to create styled buttons.
     */
    protected JButton createButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        // BasicButtonUI ensures custom background & foreground colors are honored on all platforms
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setFont(BUTTON_FONT);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                BorderFactory.createEmptyBorder(9, 18, 9, 18)
        ));

        // Interactive hover feedback
        Color hoverBg = bg.brighter();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hoverBg);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bg);
            }
        });

        return button;
    }

    /**
     * Helper to create styled primary action buttons.
     */
    protected JButton createPrimaryButton(String text) {
        return createButton(text, PRIMARY_COLOR, Color.WHITE);
    }

    /**
     * Helper to create styled accent/confirm buttons.
     */
    protected JButton createAccentButton(String text) {
        return createButton(text, ACCENT_COLOR, Color.WHITE);
    }

    /**
     * Helper to create styled danger/cancel buttons.
     */
    protected JButton createDangerButton(String text) {
        return createButton(text, DANGER_COLOR, Color.WHITE);
    }

    /**
     * Helper to create header title banner panel.
     */
    protected JPanel createHeaderPanel(String titleText, String subtitleText) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        if (subtitleText != null && !subtitleText.trim().isEmpty()) {
            JLabel subtitleLabel = new JLabel(subtitleText);
            subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            subtitleLabel.setForeground(new Color(220, 230, 242));
            headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        }

        return headerPanel;
    }

    /**
     * Displays an error alert dialog.
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays an informational message dialog.
     */
    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
