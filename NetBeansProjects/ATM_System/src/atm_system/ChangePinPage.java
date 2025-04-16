package atm_system;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.SQLException;

public class ChangePinPage extends JFrame implements ActionListener {
    
    private final User user;
    private JButton back, change;
    private JTextField pinField, repinField;
    
    public ChangePinPage(User user) {
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(null);
        setSize(900, 900);
        setLocation(300, 0);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel image = new JLabel(new ImageIcon(
            new ImageIcon(ClassLoader.getSystemResource("Icons/atm.jpg"))
                .getImage().getScaledInstance(900, 900, Image.SCALE_DEFAULT)));
        image.setBounds(0, 0, 900, 900);
        add(image);

        addComponents(image);
        setVisible(true);
    }

    private void addComponents(JLabel parent) {
        // New PIN Field
        JLabel newpinLabel = createLabel("New Pin", 165, 320);
        pinField = createTextField(330, 320);
        parent.add(newpinLabel);
        parent.add(pinField);
        
        // Confirm PIN Field
        JLabel repinLabel = createLabel("Confirm New Pin", 165, 360);
        repinField = createTextField(330, 360);
        parent.add(repinLabel);
        parent.add(repinField);

        // Buttons
        back = createButton("BACK", 170, 520);
        change = createButton("CHANGE", 355, 520);
        parent.add(back);
        parent.add(change);
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 180, 25);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("System", Font.BOLD, 16));
        return label;
    }

    private JTextField createTextField(int x, int y) {
        JTextField field = new JTextField();
        field.setFont(new Font("Raleway", Font.BOLD, 22));
        field.setBounds(x, y, 150, 25);
        return field;
    }

    private JButton createButton(String text, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 150, 30);
        btn.addActionListener(this);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(Color.BLACK);
        btn.setBackground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            navigateToDashboard();
        } else if (ae.getSource() == change) {
            handleChangePin();
        }
    }
    
    private void handleChangePin() {
        try {
            String newPin = pinField.getText().trim();
            String confirmPin = repinField.getText().trim();

            if (!validateInputs(newPin, confirmPin)) return;

            if (DatabaseManager.updatePin(user.getAccountNumber(), newPin)) {
                logTransaction();
                showSuccess();
                navigateToDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "PIN update failed");
            }
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
        }
    }

    private boolean validateInputs(String newPin, String confirmPin) {
        if (newPin.isEmpty() || confirmPin.isEmpty()) {
            showError("Please fill in all fields");
            return false;
        }

        if (!newPin.equals(confirmPin)) {
            showError("PINs do not match!");
            return false;
        }

        if (!newPin.matches("\\d{4}")) {
            showError("PIN must be 4 digits");
            return false;
        }

        return true;
    }

    private void logTransaction() throws SQLException {
        double balance = DatabaseManager.getBalance(user.getAccountNumber());
        DatabaseManager.logTransaction(
            user.getAccountNumber(), 
            "PIN Change", 
            0.00, 
            balance
        );
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void showSuccess() {
        JOptionPane.showMessageDialog(this, "PIN changed successfully!");
    }

    private void navigateToDashboard() {
        dispose();
        new Dashboard(user).setVisible(true);
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }
}