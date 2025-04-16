package atm_system;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.*;

public class Login extends JFrame implements ActionListener {

    private JButton loginBtn, clearBtn, signupBtn;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public Login() {
        initializeUI();
    }
    
   

    private void initializeUI() {
        setTitle("FS Banking System - Login");
        setLayout(null);
        setSize(800, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Logo
        ImageIcon logoIcon = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        JLabel logoLabel = new JLabel(new ImageIcon(logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        logoLabel.setBounds(40, 10, 100, 100);
        add(logoLabel);

        // Welcome Text
        JLabel welcomeLabel = new JLabel("Welcome To FS Banking System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        welcomeLabel.setBounds(200, 40, 600, 50);
        add(welcomeLabel);

        // Login Panel
        JPanel loginPanel = createLoginPanel();
        add(loginPanel);

        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(230, 230, 250));
        panel.setBounds(200, 150, 400, 270);

        // Username
        JLabel usernameLabel = new JLabel("Account Number:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameLabel.setBounds(20, 20, 120, 30);
        panel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 20, 230, 30);
        panel.add(usernameField);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setBounds(20, 70, 120, 30);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 70, 230, 30);
        panel.add(passwordField);

        // Buttons
        loginBtn = createButton("LOGIN", Color.GREEN.darker(), 80, 120);
        loginBtn.addActionListener(this);
        panel.add(loginBtn);

        clearBtn = createButton("CLEAR", Color.RED, 220, 120);
        clearBtn.addActionListener(this);
        panel.add(clearBtn);

        JLabel orLabel = new JLabel("OR", SwingConstants.CENTER);
        orLabel.setBounds(150, 160, 100, 30);
        panel.add(orLabel);

        signupBtn = createButton("CREATE ACCOUNT", Color.BLUE, 80, 200);
        signupBtn.setPreferredSize(new Dimension(200, 30));
        signupBtn.addActionListener(this);
        panel.add(signupBtn);

        return panel;
    }

    private JButton createButton(String text, Color bgColor, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 100, 30);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            if (ae.getSource() == loginBtn) {
                handleLogin();
            } else if (ae.getSource() == clearBtn) {
                clearFields();
            } else if (ae.getSource() == signupBtn) {
                openSignup();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void handleLogin() throws SQLException {
        String accountNumber = usernameField.getText().trim();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);
        
        if (accountNumber.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }

        User user = DatabaseManager.authenticateUser(accountNumber, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            dispose();
            new Dashboard(user);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!");
        }
        Arrays.fill(passwordChars, ' ');
        passwordField.setText("");
    }

    private void handleDatabaseError(SQLException ex) {
        if (ex.getErrorCode() == 0) {
            JOptionPane.showMessageDialog(this, "Database connection failed!");
        } else {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }

    private void openSignup() {
        dispose();
        new Signup().setVisible(true);
    }

    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> new Login());
    }
}