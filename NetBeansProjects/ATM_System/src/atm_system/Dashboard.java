package atm_system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Dashboard extends JFrame implements ActionListener {

    private final User user;
    private final JButton deposit, withdrawal, cashsend, statement, changepin, balanceinquiry, exit;

    public Dashboard(User user) {
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
        JLabel text = new JLabel("Please select your Transaction");
        text.setBounds(220, 300, 700, 35);
        text.setForeground(Color.WHITE);
        text.setFont(new Font("System", Font.BOLD, 16));
        image.add(text);

        // Buttons
        deposit = createButton("DEPOSIT", 170, 415, image);
        withdrawal = createButton("WITHDRAWAL", 355, 415, image);
        cashsend = createButton("CASH SEND", 170, 450, image);
        statement = createButton("STATEMENT", 355, 450, image);
        changepin = createButton("CHANGE PIN", 170, 485, image);
        balanceinquiry = createButton("BALANCE INQUIRY", 355, 485, image);
        exit = createButton("EXIT", 355, 520, image);

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
        parent.add(btn);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == deposit) {
            setVisible(false);
            new DepositPage(user).setVisible(true);
        } else if (ae.getSource() == withdrawal) {
            setVisible(false);
            new WithdrawPage(user).setVisible(true);
        } else if (ae.getSource() == cashsend) {
            setVisible(false);
            new CashSendPage(user).setVisible(true);
        } else if (ae.getSource() == statement) {
            setVisible(false);
            new StatementPage(user).setVisible(true);
        } else if (ae.getSource() == changepin) {
            setVisible(false);
            new ChangePinPage(user).setVisible(true);
        } else if (ae.getSource() == balanceinquiry) {
            setVisible(false);
            new BalancePage(user).setVisible(true);
        } else if (ae.getSource() == exit) {
            System.exit(0);
        }
    }
}
