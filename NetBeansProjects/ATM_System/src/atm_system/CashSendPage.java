package atm_system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class CashSendPage extends JFrame implements ActionListener {
    
    private final User user;
    private  JButton back, send;
    private  JTextField amountField, accountField;
    
    public CashSendPage(User user) {
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
                .getImage().getScaledInstance(900, 900, Image.SCALE_DEFAULT)
        ));
        image.setBounds(0, 0, 900, 900);
        add(image);

        addComponents(image);
        setVisible(true);
    }

    private void addComponents(JLabel parent) {
        JLabel recipientLabel = new JLabel("Account no.");
        recipientLabel.setBounds(165, 320, 180, 25);
        recipientLabel.setForeground(Color.WHITE);
        recipientLabel.setFont(new Font("System", Font.BOLD, 16));
        parent.add(recipientLabel);
        
        accountField = createTextField(330, 320);
        parent.add(accountField);
        
        JLabel amountLabel = new JLabel("Enter amount");
        amountLabel.setBounds(165, 360, 180, 25);
        amountLabel.setForeground(Color.WHITE);
        amountLabel.setFont(new Font("System", Font.BOLD, 16));
        parent.add(amountLabel);
        
        amountField = createTextField(330, 360);
        parent.add(amountField);

        back = createButton("BACK", 170, 520, parent);
        send = createButton("SEND", 355, 520, parent);
    }

    private JTextField createTextField(int x, int y) {
        JTextField field = new JTextField();
        field.setFont(new Font("Raleway", Font.BOLD, 22));
        field.setBounds(x, y, 150, 25);
        return field;
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
            dispose();
            new Dashboard(user).setVisible(true);
        } else if (ae.getSource() == send) {
            handleCashSend();
        }
    }
    
    private void handleCashSend() {
        try {
            String recipient = accountField.getText();
            double amount = Double.parseDouble(amountField.getText());
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive!");
                return;
            }
            
            if (!DatabaseManager.accountExists(recipient)) {
                JOptionPane.showMessageDialog(this, "Recipient account not found!");
                return;
            }
            
            if (DatabaseManager.transferCash(user.getAccountNumber(), recipient, amount)) {
                JOptionPane.showMessageDialog(this, "Transfer successful!");
                dispose();
                new Dashboard(user).setVisible(true);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount format!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // Required to properly implement setVisible
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }
}