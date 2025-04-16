/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package atm_system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sizwe
 */
public class DepositPage extends JFrame implements ActionListener {
    
    private final User user;
    private final JButton back, deposit;
    private final JTextField amountField; // Added as class field
    
    public DepositPage(User user) {
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
        JLabel text = new JLabel("Amount you want to deposit");
        text.setBounds(220, 300, 700, 35);
        text.setForeground(Color.WHITE);
        text.setFont(new Font("System", Font.BOLD, 16));
        image.add(text);
        
        amountField = new JTextField(); // Initialize field
        amountField.setFont(new Font("Raleway", Font.BOLD, 22));
        amountField.setBounds(170, 350, 320, 25);
        image.add(amountField);

        // Buttons
        back = createButton("BACK", 170, 520, image);
        deposit = createButton("DEPOSIT", 355, 520, image);
        setSize(900, 900);
        setLocation(300, 0);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            setVisible(false);
            new Dashboard(user).setVisible(true);
        } else if (ae.getSource() == deposit) {
            try {
                handleDeposit();
            } catch (SQLException ex) {
                Logger.getLogger(DepositPage.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
    }
    
    private void handleDeposit() throws SQLException {
        try {
            double amount = Double.parseDouble(amountField.getText());
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive!");
                return;
            }
            
            // Update balance
            boolean success = DatabaseManager.updateBalance(user.getAccountNumber(), amount);
            
            if (success) {
                // Log transaction
                double newBalance = DatabaseManager.getBalance(user.getAccountNumber());
                DatabaseManager.logTransaction(
                    user.getAccountNumber(), 
                    "Deposit", 
                    amount, 
                    newBalance
                );
                
                JOptionPane.showMessageDialog(this, 
                    "Deposit successful!\nNew Balance: ZAR " + String.format("%,.2f", newBalance)
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
}
