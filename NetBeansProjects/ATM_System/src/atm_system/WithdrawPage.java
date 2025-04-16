package atm_system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class WithdrawPage extends JFrame implements ActionListener {
    
    private final User user;
    private final JButton back, withdraw;
    private final JTextField amountField; // Changed to class field
    
    public WithdrawPage(User user) {
        this.user = user;

        setLayout(null);

        // Background image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("Icons/atm.jpg"));
        Image i2 = i1.getImage().getScaledInstance(900, 900, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(0, 0, 900, 900);
        add(image);

        // Text label
        JLabel text = new JLabel("Amount you want to withdraw");
        text.setBounds(220, 300, 700, 35);
        text.setForeground(Color.WHITE);
        text.setFont(new Font("System", Font.BOLD, 16));
        image.add(text);
        
        amountField = new JTextField();
        amountField.setFont(new Font("Raleway", Font.BOLD, 22));
        amountField.setBounds(170, 350, 320, 25);
        image.add(amountField);

        // Buttons
        back = createButton("BACK", 170, 520, image);
        withdraw = createButton("WITHDRAW", 355, 520, image);
        setSize(900, 900);
        setLocation(300, 0);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JButton createButton(String text, int x, int y, JLabel parent) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 150, 30);
        btn.addActionListener(this);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(Color.BLACK);
        btn.setBackground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        parent.add(btn);
        return btn;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            setVisible(false);
            new Dashboard(user).setVisible(true);
        } else if (ae.getSource() == withdraw) {
            handleWithdrawal();
        } 
    }
    
    private void handleWithdrawal() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive!");
                return;
            }
            
            // Check available balance
            double currentBalance = DatabaseManager.getBalance(user.getAccountNumber());
            if (amount > currentBalance) {
                JOptionPane.showMessageDialog(this, "Insufficient funds!");
                return;
            }
            
            // Process withdrawal
            boolean success = DatabaseManager.updateBalance(user.getAccountNumber(), -amount);
            
            if (success) {
                // Log transaction
                double newBalance = currentBalance - amount;
                DatabaseManager.logTransaction(
                    user.getAccountNumber(), 
                    "Withdrawal", 
                    amount, 
                    newBalance
                );
                
                JOptionPane.showMessageDialog(this, 
                    "Withdrawal successful!\nNew Balance: ZAR " + String.format("%,.2f", newBalance)
                );
                setVisible(false);
                new Dashboard(user).setVisible(true);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount format!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }
}