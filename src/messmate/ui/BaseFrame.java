package messmate.ui;

import javax.swing.*;

/**
 * Base frame class providing common window configuration.
 * Demonstrates Syllabus Module 2 (Inheritance: Superclass BaseFrame).
 */
public abstract class BaseFrame extends JFrame {

    public BaseFrame(String title, int width, int height) {
        super(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
