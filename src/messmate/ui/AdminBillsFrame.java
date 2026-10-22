package messmate.ui;

import messmate.model.Admin;
import messmate.model.MealPricing;
import messmate.model.StudentBillSummary;
import messmate.service.BookingService;
import messmate.service.IBookingService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/**
 * Admin frame to view monthly bills of each student in a table.
 * Demonstrates Syllabus:
 * - Module 1: Java Swing GUI components (JTable, JComboBox, JTextField, JLabel, JButton)
 * - Module 2: Event-driven programming (ActionListener, DocumentListener)
 * - Module 3: Interfaces (IBookingService)
 * - Module 4: Relational persistence and aggregated data visualization
 */
public class AdminBillsFrame extends BaseFrame {

    private Admin currentAdmin;
    private JFrame parentDashboard;
    private IBookingService bookingService;

    private JComboBox<String> cmbMonth;
    private JComboBox<Integer> cmbYear;
    private JTextField txtSearch;
    private JTable tblBills;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JLabel lblSummary;

    public AdminBillsFrame(Admin admin, JFrame parent) {
        super("Messmate - Student Monthly Bills", 780, 520);
        this.currentAdmin = admin;
        this.parentDashboard = parent;
        this.bookingService = new BookingService();
        initUI();
        loadBills();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // ==========================================
        // 1. Top Panel: Month/Year Filter & Search
        // ==========================================
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        // Row 1: Month, Year, Load Button
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        filterRow.add(new JLabel("Month:"));
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        cmbMonth = new JComboBox<>(months);
        LocalDate now = LocalDate.now();
        cmbMonth.setSelectedIndex(now.getMonthValue() - 1);
        filterRow.add(cmbMonth);

        filterRow.add(new JLabel("Year:"));
        Integer[] years = new Integer[]{
                now.getYear() - 2, now.getYear() - 1, now.getYear(), now.getYear() + 1
        };
        cmbYear = new JComboBox<>(years);
        cmbYear.setSelectedItem(now.getYear());
        filterRow.add(cmbYear);

        JButton btnView = new JButton("View Bills");
        btnView.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnView.addActionListener(e -> loadBills());
        filterRow.add(btnView);

        // Fixed rate info indicator
        JLabel lblRatesInfo = new JLabel(String.format(
                "  [ Rates: Breakfast ₹%d | Lunch ₹%d | Snacks ₹%d | Dinner ₹%d ]",
                (int) MealPricing.BREAKFAST_PRICE, (int) MealPricing.LUNCH_PRICE,
                (int) MealPricing.SNACKS_PRICE, (int) MealPricing.DINNER_PRICE
        ));
        lblRatesInfo.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblRatesInfo.setForeground(new Color(90, 90, 90));
        filterRow.add(lblRatesInfo);

        topContainer.add(filterRow);

        // Row 2: Search Box
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchRow.add(new JLabel("Quick Search (ID / Name):"));
        txtSearch = new JTextField(18);
        searchRow.add(txtSearch);

        JButton btnClearSearch = new JButton("Clear");
        btnClearSearch.addActionListener(e -> txtSearch.setText(""));
        searchRow.add(btnClearSearch);

        topContainer.add(searchRow);
        add(topContainer, BorderLayout.NORTH);

        // ==========================================
        // 2. Center Panel: Bills JTable
        // ==========================================
        Vector<String> columns = new Vector<>();
        columns.add("ID No");
        columns.add("Student Name");
        columns.add("Breakfast (₹30)");
        columns.add("Lunch (₹50)");
        columns.add("Snacks (₹20)");
        columns.add("Dinner (₹40)");
        columns.add("Total Meals");
        columns.add("Total Bill (₹)");

        tableModel = new DefaultTableModel(new Vector<Vector<Object>>(), columns) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblBills = new JTable(tableModel);
        tblBills.setRowHeight(24);
        tblBills.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tblBills.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Center-align numeric columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblBills.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID No
        for (int c = 2; c <= 6; c++) {
            tblBills.getColumnModel().getColumn(c).setCellRenderer(centerRenderer);
        }

        // Right-align currency column
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblBills.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);

        // Set column widths
        tblBills.getColumnModel().getColumn(0).setPreferredWidth(90);
        tblBills.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblBills.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblBills.getColumnModel().getColumn(3).setPreferredWidth(85);
        tblBills.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblBills.getColumnModel().getColumn(5).setPreferredWidth(85);
        tblBills.getColumnModel().getColumn(6).setPreferredWidth(85);
        tblBills.getColumnModel().getColumn(7).setPreferredWidth(100);

        // Sorter for instant searching
        rowSorter = new TableRowSorter<>(tableModel);
        tblBills.setRowSorter(rowSorter);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        add(new JScrollPane(tblBills), BorderLayout.CENTER);

        // ==========================================
        // 3. Bottom Panel: Totals Summary & Back Button
        // ==========================================
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 12, 15));

        lblSummary = new JLabel("Total Students: 0 | Total Meals Booked: 0 | Total Monthly Collection: ₹ 0.00", JLabel.CENTER);
        lblSummary.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblSummary.setForeground(new Color(15, 60, 110));
        lblSummary.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(lblSummary);

        bottomPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        JButton btnBack = new JButton("Back to Dashboard");
        btnBack.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(e -> {
            parentDashboard.setVisible(true);
            dispose();
        });
        bottomPanel.add(btnBack);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void filterTable() {
        String text = txtSearch.getText().trim();
        if (text.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0, 1));
        }
    }

    /**
     * Loads monthly bills for all students from MySQL.
     */
    private void loadBills() {
        int selectedMonth = cmbMonth.getSelectedIndex() + 1;
        int selectedYear = (Integer) cmbYear.getSelectedItem();

        tableModel.setRowCount(0);

        int totalStudents = 0;
        int totalMeals = 0;
        double totalRevenue = 0.0;

        try {
            List<StudentBillSummary> summaries = bookingService.getMonthlyBillsForAllStudents(selectedMonth, selectedYear);

            for (StudentBillSummary s : summaries) {
                Vector<Object> row = new Vector<>();
                row.add(s.getIdNumber());
                row.add(s.getStudentName());
                row.add(s.getBreakfastCount());
                row.add(s.getLunchCount());
                row.add(s.getSnacksCount());
                row.add(s.getDinnerCount());
                row.add(s.getTotalMealsCount());
                row.add(String.format("₹ %.2f", s.getTotalBill()));

                tableModel.addRow(row);

                totalStudents++;
                totalMeals += s.getTotalMealsCount();
                totalRevenue += s.getTotalBill();
            }

            String monthName = Month.of(selectedMonth).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            lblSummary.setText(String.format(
                    "[%s %d]  Total Students: %d  |  Total Meals: %d  |  Total Monthly Collection: ₹ %.2f",
                    monthName, selectedYear, totalStudents, totalMeals, totalRevenue
            ));

        } catch (SQLException ex) {
            showError("Database error while loading student bills: " + ex.getMessage());
        }
    }
}
