package atm_system;

import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;

public class StatementPage extends JFrame implements ActionListener {
    
    private final User user;
    private JButton back;
    private JTextArea statementArea;
    
    public StatementPage(User user) {
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
                .getImage().getScaledInstance(900, 900, Image.SCALE_DEFAULT)));
        image.setBounds(0, 0, 900, 900);
        add(image);

        addComponents(image);
        setVisible(true);
    }

    private void addComponents(JLabel parent) {
        // Title
        JLabel titleLabel = new JLabel("Recent Transactions");
        titleLabel.setBounds(200, 200, 500, 35);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("System", Font.BOLD, 20));
        parent.add(titleLabel);

        // Statement Area
        statementArea = new JTextArea();
        statementArea.setEditable(false);
        statementArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        statementArea.setForeground(Color.BLACK);
        statementArea.setBackground(new Color(240, 240, 240));
        
        JScrollPane scrollPane = new JScrollPane(statementArea);
        scrollPane.setBounds(150, 250, 600, 400);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        parent.add(scrollPane);

        // Load transactions
        loadTransactions();

        // Back Button
        back = new JButton("BACK");
        back.setBounds(355, 670, 150, 30);
        back.addActionListener(this);
        back.setFont(new Font("Arial", Font.BOLD, 12));
        back.setForeground(Color.BLACK);
        back.setBackground(Color.WHITE);
        back.setOpaque(true);
        back.setBorderPainted(false);
        parent.add(back);
    }

    private void loadTransactions() {
        try {
            List<String> transactions = DatabaseManager.getRecentTransactions(
                user.getAccountNumber(), 
                10 // Last 10 transactions
            );
            
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-12s %-15s %-10s %-15s\n", 
                "Date", "Type", "Amount", "Balance"));
            sb.append("------------------------------------------------\n");
            
            for (String transaction : transactions) {
                sb.append(transaction).append("\n");
            }
            
            statementArea.setText(sb.toString());
        } catch (SQLException ex) {
            statementArea.setText("Error loading transactions\n" + ex.getMessage());
        }
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