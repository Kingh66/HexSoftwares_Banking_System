package atm_system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.Arrays;

public class SignupPage2 extends JFrame implements ActionListener {
    // Add chequeBookCheck declaration
    private JCheckBox atmCardCheck, mobileBankingCheck, chequeBookCheck, smsAlertsCheck;
    private JRadioButton savingsBtn, currentBtn;
    private JPasswordField pinField, passwordField;
    private JCheckBox declarationCheck;
    private JButton submitBtn, cancelBtn;
    private final User user;

    public SignupPage2(User user) {
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Account Setup - Banking Details");
        setSize(800, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        addHeader();
        addAccountTypeSection();
        addServicesSection();
        addSecurityFields();
        addDeclaration();
        addButtons();
        
        setVisible(true);
    }

    private void addHeader() {
        JLabel heading = new JLabel("Finalize Your Account Setup");
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setBounds(150, 30, 500, 30);
        add(heading);
    }

    private void addAccountTypeSection() {
        JLabel label = new JLabel("Account Type:");
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setBounds(100, 100, 150, 30);
        add(label);

        savingsBtn = createRadioButton("Savings", 300, 100);
        currentBtn = createRadioButton("Current", 400, 100);
        
        ButtonGroup group = new ButtonGroup();
        group.add(savingsBtn);
        group.add(currentBtn);
    }

    private JRadioButton createRadioButton(String text, int x, int y) {
        JRadioButton btn = new JRadioButton(text);
        btn.setBounds(x, y, 100, 30);
        btn.setBackground(Color.WHITE);
        add(btn);
        return btn;
    }

    private void addServicesSection() {
        JLabel label = new JLabel("Additional Services:");
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setBounds(100, 150, 150, 30);
        add(label);

        // Assign checkboxes to class variables
        atmCardCheck = createCheckBox("Online Banking", 300, 150);
        mobileBankingCheck = createCheckBox("Mobile Banking", 300, 190);
        chequeBookCheck = createCheckBox("Cheque Book", 300, 230);
        smsAlertsCheck = createCheckBox("SMS Alerts", 300, 270);
    }

    private JCheckBox createCheckBox(String text, int x, int y) {
        JCheckBox cb = new JCheckBox(text);
        cb.setBounds(x, y, 150, 30);
        cb.setBackground(Color.WHITE);
        add(cb);
        return cb;
    }

    private void addSecurityFields() {
        addPasswordField("PIN (4 digits):", 100, 280, pinField = new JPasswordField());
        addPasswordField("Password:", 100, 330, passwordField = new JPasswordField());
    }

    private void addPasswordField(String labelText, int x, int y, JPasswordField field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setBounds(x, y, 150, 30);
        add(label);

        field.setBounds(300, y, 350, 30);
        add(field);
    }

    private void addDeclaration() {
        declarationCheck = new JCheckBox("I agree to the terms and conditions");
        declarationCheck.setFont(new Font("Arial", Font.PLAIN, 14));
        declarationCheck.setBounds(100, 380, 550, 30);
        declarationCheck.setBackground(Color.WHITE);
        add(declarationCheck);
    }

    private void addButtons() {
        submitBtn = createButton("Submit", Color.GREEN.darker(), 550, 450);
        cancelBtn = createButton("Cancel", Color.RED, 400, 450);
    }

    private JButton createButton(String text, Color color, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 100, 35);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(this);
        add(btn);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == submitBtn) {
                handleSubmission();
            } else if (e.getSource() == cancelBtn) {
                handleCancellation();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void handleSubmission() {
        if (!validateInputs()) return;
        
        try {
            User completeUser = prepareUserObject();
            if (DatabaseManager.createUser(completeUser)) {
                showConfirmation(completeUser);
            } else {
                JOptionPane.showMessageDialog(this, "Account creation failed. Please try again.");
            }
        } catch (SQLException ex) {
            handleDatabaseError(ex);
        } finally {
            clearSensitiveData();
        }
    }

    private boolean validateInputs() {
        if (!declarationCheck.isSelected()) {
            showError("You must accept the terms and conditions");
            return false;
        }

        if (!savingsBtn.isSelected() && !currentBtn.isSelected()) {
            showError("Please select an account type");
            return false;
        }

        String pin = new String(pinField.getPassword());
        String password = new String(passwordField.getPassword());

        if (!isValidPIN(pin)) {
            showError("PIN must be 4 numeric digits");
            return false;
        }

        if (!isValidPassword(password)) {
            showError("Password must be 8+ characters with mix of letters and numbers");
            return false;
        }

        return true;
    }

    private boolean isValidPIN(String pin) {
        return pin.matches("\\d{4}");
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 && 
               password.matches(".*\\d.*") && 
               password.matches(".*[a-zA-Z].*");
    }

    private User prepareUserObject() {
        String accountType = savingsBtn.isSelected() ? "Savings" : "Current";
        String services = getSelectedServices();
        String pin = new String(pinField.getPassword());
        String password = new String(passwordField.getPassword());

        user.setAccountType(accountType);
        user.setServices(services);
        user.setPin(pin);
        user.setPassword(password);
        user.setAccountNumber(DatabaseManager.generateNewAccountNumber());
        
        return user;
    }

    private String getSelectedServices() {
        StringBuilder services = new StringBuilder();
        if (atmCardCheck.isSelected()) services.append("Online Banking, ");
        if (mobileBankingCheck.isSelected()) services.append("Mobile Banking, ");
        if (chequeBookCheck.isSelected()) services.append("Cheque Book, ");
        if (smsAlertsCheck.isSelected()) services.append("SMS Alerts, ");

        return services.length() > 0 ? 
            services.substring(0, services.length() - 2) : 
            "None";
    }

    private void showConfirmation(User user) {
        dispose();
        new AccountSummary(user).setVisible(true);
    }

    private void handleDatabaseError(SQLException ex) {
        if (ex.getErrorCode() == 1062) {
            showError("Account number conflict. Please try again.");
        } else {
            showError("Database error: " + ex.getMessage());
        }
    }

    private void clearSensitiveData() {
        Arrays.fill(pinField.getPassword(), '0');
        Arrays.fill(passwordField.getPassword(), '0');
        pinField.setText("");
        passwordField.setText("");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    private void handleCancellation() {
        dispose();
        new Signup().setVisible(true);
    }
}