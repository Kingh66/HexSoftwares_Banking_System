package atm_system;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.SQLException;

public class BalancePage extends JFrame implements ActionListener {
    
    private final User user;
    private JButton back;
    
    public BalancePage(User user) {
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(null);
        setSize(900, 900);
        setLocation(300, 0);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Background image
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
        // Balance Display
        JLabel balanceLabel = createLabel("Current Balance:", 165, 300);
        JLabel amountLabel = createBalanceAmountLabel(330, 300);
        
        // Get updated balance from database
        try {
            double balance = DatabaseManager.getBalance(user.getAccountNumber());
            amountLabel.setText("ZAR " + String.format("%,.2f", balance));
        } catch (SQLException ex) {
            amountLabel.setText("Error loading");
            JOptionPane.showMessageDialog(this, "Failed to fetch balance: " + ex.getMessage());
        }

        parent.add(balanceLabel);
        parent.add(amountLabel);

        // Back Button
        back = createButton("BACK", 355, 520);
        parent.add(back);
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 300, 35);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("System", Font.BOLD, 18));
        return label;
    }

    private JLabel createBalanceAmountLabel(int x, int y) {
        JLabel label = new JLabel();
        label.setBounds(x, y, 300, 35);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Raleway", Font.BOLD, 22));
        return label;
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
            dispose();
            new Dashboard(user).setVisible(true);
        }
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }
}