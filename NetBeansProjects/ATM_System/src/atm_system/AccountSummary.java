package atm_system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AccountSummary extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private static final Color SECONDARY_COLOR = new Color(34, 34, 34);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font VALUE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final User user;

    // Single constructor using User object
    public AccountSummary(User user) {
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Account Summary - FS Banking System");
        setSize(850, 700);  // Adjusted for better vertical fit
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = createMainPanel();
        addHeader(mainPanel);
        addUserDetails(mainPanel);
        addServicesSection(mainPanel);
        addNavigationButton(mainPanel);
        
        setContentPane(mainPanel);
        setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }

    // Removed redundant methods and fixed implementation
    private void addHeader(JPanel panel) {
        JLabel heading = new JLabel("Account Created Successfully");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setForeground(PRIMARY_COLOR);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(heading);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
    }

    private void addUserDetails(JPanel panel) {
        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        detailsPanel.setBackground(Color.WHITE);
        
        // Added null safety for user data
        addDetailRow(detailsPanel, "Account Number:", safeGet(user.getAccountNumber()));
        addDetailRow(detailsPanel, "Full Name:", safeGet(user.getFullName()));
        addDetailRow(detailsPanel, "ID Number:", safeGet(user.getIdNumber()));
        addDetailRow(detailsPanel, "Date of Birth:", safeGet(user.getDob()));
        addDetailRow(detailsPanel, "Email:", safeGet(user.getEmail()));
        addDetailRow(detailsPanel, "Phone Number:", safeGet(user.getPhone()));
        addDetailRow(detailsPanel, "Gender:", safeGet(user.getGender()));
        addDetailRow(detailsPanel, "Address:", safeGet(user.getAddress()));
        addDetailRow(detailsPanel, "Account Type:", safeGet(user.getAccountType()));
        addDetailRow(detailsPanel, "Nationality:", safeGet(user.getNationality()));
        addDetailRow(detailsPanel, "PIN:", maskSensitiveInfo(safeGet(user.getPin())));
        addDetailRow(detailsPanel, "Password:", maskSensitiveInfo(safeGet(user.getPassword())));
        
        panel.add(detailsPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
    }

    private String safeGet(String value) {
        return value != null ? value : "N/A";
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(LABEL_FONT);
        lbl.setForeground(PRIMARY_COLOR);
        panel.add(lbl);

        JLabel val = new JLabel(value);
        val.setFont(VALUE_FONT);
        val.setForeground(SECONDARY_COLOR);
        panel.add(val);
    }

    private void addServicesSection(JPanel panel) {
        JLabel servicesLabel = new JLabel("Selected Services:");
        servicesLabel.setFont(LABEL_FONT);
        servicesLabel.setForeground(PRIMARY_COLOR);
        servicesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(servicesLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextArea servicesArea = new JTextArea(formatServices(user.getServices()));
        servicesArea.setFont(VALUE_FONT);
        servicesArea.setEditable(false);
        servicesArea.setLineWrap(true);
        servicesArea.setWrapStyleWord(true);
        servicesArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane scrollPane = new JScrollPane(servicesArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setPreferredSize(new Dimension(800, 80));
        panel.add(scrollPane);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
    }

    private String formatServices(String services) {
        if (services == null || services.isEmpty()) {
            return "• No additional services selected";
        }
        return "• " + services.replaceAll(", ", "\n• ");
    }

    private String maskSensitiveInfo(String input) {
        if (input == null || input.isEmpty()) return "";
        return "*".repeat(input.length());
    }

    private void addNavigationButton(JPanel panel) {
        JButton closeButton = new JButton("Return to Login");
        styleButton(closeButton);
        closeButton.addActionListener(e -> returnToLogin());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel);
    }

    private void styleButton(JButton button) {
        button.setFont(LABEL_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(220, 20, 60));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(178, 34, 34));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(220, 20, 60));
            }
        });
    }

    private void returnToLogin() {
        this.dispose();
        EventQueue.invokeLater(() -> new Login().setVisible(true));
    }
}