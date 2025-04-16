package atm_system;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Pattern;

public class Signup extends JFrame implements ActionListener {

    private JTextField nameField, idField, emailField, phoneField, addressField, nationalityField;
    private JComboBox<String> genderBox;
    private JButton nextBtn, cancelBtn;
    private JDateChooser dobChooser;

    public Signup() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("New Account Registration - Personal Details");
        setLayout(null);
        setSize(800, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        addHeader();
        addFormFields();
        addButtons();
        setVisible(true);
    }

    private void addHeader() {
        JLabel heading = new JLabel("Create Your Account - Personal Details");
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setBounds(150, 30, 500, 30);
        add(heading);
    }

    private void addFormFields() {
        int y = 100;
        nameField = createTextField("Full Name:", 100, y);
        idField = createTextField("ID Number:", 100, y += 50);
        addDateField(y += 50);
        emailField = createTextField("Email Address:", 100, y += 50);
        phoneField = createTextField("Phone Number:", 100, y += 50);
        addGenderField(y += 50);
        addressField = createTextField("Address:", 100, y += 50);
        nationalityField = createTextField("Nationality:", 100, y += 50);
    }

    private JTextField createTextField(String label, int x, int y) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        jLabel.setBounds(x, y, 150, 30);
        add(jLabel);

        JTextField textField = new JTextField();
        textField.setBounds(x + 200, y, 350, 30);
        add(textField);
        return textField;
    }

    private void addDateField(int y) {
        JLabel dobLabel = new JLabel("Date of Birth:");
        dobLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dobLabel.setBounds(100, y, 150, 30);
        add(dobLabel);

        dobChooser = new JDateChooser();
        dobChooser.setBounds(300, y, 350, 30);
        dobChooser.setDateFormatString("yyyy-MM-dd");
        dobChooser.setMaxSelectableDate(new java.util.Date());
        add(dobChooser);
    }

    private void addGenderField(int y) {
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        genderLabel.setBounds(100, y, 150, 30);
        add(genderLabel);

        String[] genders = {"Select", "Male", "Female", "Other"};
        genderBox = new JComboBox<>(genders);
        genderBox.setBounds(300, y, 350, 30);
        add(genderBox);
    }

    private void addButtons() {
        nextBtn = createButton("Next", Color.GREEN.darker(), 550, 510);
        cancelBtn = createButton("Cancel", Color.RED, 400, 510);
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
            if (e.getSource() == nextBtn) {
                handleNextButton();
            } else if (e.getSource() == cancelBtn) {
                handleCancelButton();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void handleNextButton() {
        try {
            User newUser = validateAndCreateUser();
            checkExistingRecords(newUser);
            proceedToNextStep(newUser);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private User validateAndCreateUser() {
        String name = nameField.getText().trim();
        String id = idField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String address = addressField.getText().trim();
        String nationality = nationalityField.getText().trim();
        
        // Get date as String in yyyy-MM-dd format
        String dob = getFormattedDate();

        validateInput(name, id, email, phone, gender, address, nationality);

        return new User(
            null, // Account number will be generated later
            name,
            id,
            dob,
            email,
            phone,
            gender,
            address,
            nationality,
            null, // Account type
            null, // Services
            null, // PIN
            null, // Password
            0.00
        );
    }

    private String getFormattedDate() {
        if (dobChooser.getDate() == null) {
            throw new IllegalArgumentException("Please select a valid date of birth");
        }
        LocalDate dob = dobChooser.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return dob.toString();
    }

    private void validateInput(String name, String id, String email, String phone, 
                              String gender, String address, String nationality) {
        if (name.isEmpty() || id.isEmpty() || email.isEmpty() || phone.isEmpty() ||
            address.isEmpty() || nationality.isEmpty()) {
            throw new IllegalArgumentException("All fields are required!");
        }

        if (gender.equals("Select")) {
            throw new IllegalArgumentException("Please select a gender!");
        }

        if (!Pattern.matches("^[A-Za-z ]+$", name)) {
            throw new IllegalArgumentException("Invalid name format! Use only letters and spaces");
        }

        if (!Pattern.matches("^\\d{10,13}$", phone)) {
            throw new IllegalArgumentException("Phone number must be 10-13 digits!");
        }

        if (!Pattern.matches("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$", email)) {
            throw new IllegalArgumentException("Invalid email format!");
        }
    }

    private void checkExistingRecords(User user) throws SQLException {
        if (DatabaseManager.checkExistingUser(user.getEmail(), user.getPhone(), user.getIdNumber())) {
            throw new IllegalArgumentException("User with this email/phone/ID already exists!");
        }
    }

    private void proceedToNextStep(User user) {
        dispose();
        new SignupPage2(user).setVisible(true);
    }

    private void handleCancelButton() {
        dispose();
        new Login().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Signup::new);
    }
}