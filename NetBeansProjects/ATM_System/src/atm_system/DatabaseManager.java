package atm_system;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "*******";
    private static final int MAX_RETRIES = 3;
    private static final int ACCOUNT_NUMBER_LENGTH = 10;

    static {
        initializeDatabaseDriver();
    }

    private static void initializeDatabaseDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            verifyDatabaseStructure();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", DB_USER);
        props.setProperty("password", DB_PASSWORD);
        props.setProperty("useSSL", "false");
        props.setProperty("allowPublicKeyRetrieval", "true"); // Add this
        props.setProperty("serverTimezone", "UTC"); // Add this for timezone
        return DriverManager.getConnection(DB_URL, props);
    }
    
    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Connection successful!");
            System.out.println("Server version: " + conn.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {
            System.err.println("Connection failed:");
            e.printStackTrace();
        }
    }

    public static boolean createUser(User user) throws SQLException {
        final String sql = "INSERT INTO users (account_number, full_name, id_number, dob, email, phone, "
                + "gender, address, nationality, account_type, services, pin_hash, password_hash) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                bindUserParameters(ps, user);
                if (ps.executeUpdate() > 0) return true;
                
            } catch (SQLException e) {
                handleCreateUserException(e, user);
            }
        }
        return false;
    }

    private static void bindUserParameters(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getAccountNumber());
        ps.setString(2, user.getFullName());
        ps.setString(3, user.getIdNumber());
        ps.setDate(4, Date.valueOf(user.getDob()));
        ps.setString(5, user.getEmail());
        ps.setString(6, user.getPhone());
        ps.setString(7, user.getGender());
        ps.setString(8, user.getAddress());
        ps.setString(9, user.getNationality());
        ps.setString(10, user.getAccountType());
        ps.setString(11, user.getServices());
        ps.setString(12, SecurityUtil.hashPassword(user.getPin()));
        ps.setString(13, SecurityUtil.hashPassword(user.getPassword()));
    }

    private static void handleCreateUserException(SQLException e, User user) throws SQLException {
        if (e.getErrorCode() == 1062) { // Duplicate entry
            user.setAccountNumber(generateNewAccountNumber());
            return;
        }
        throw e;
    }

    public static User authenticateUser(String accountNumber, String password) throws SQLException {
        final String sql = "SELECT account_number, full_name, id_number, dob, email, phone, "
                + "gender, address, nationality, account_type, services, balance "
                + "FROM users WHERE account_number = ? AND password_hash = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, accountNumber);
            ps.setString(2, SecurityUtil.hashPassword(password));
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToUser(rs) : null;
            }
        }
    }

    private static User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getString("account_number"),
            rs.getString("full_name"),
            rs.getString("id_number"),
            rs.getDate("dob").toLocalDate().toString(),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("gender"),
            rs.getString("address"),
            rs.getString("nationality"),
            rs.getString("account_type"),
            rs.getString("services"),
            "", // PIN not returned
            "", // Password not returned
            rs.getDouble("balance")
        );
    }

    public static boolean checkExistingUser(String email, String phone, String idNumber) throws SQLException {
        final String sql = "SELECT COUNT(*) FROM users WHERE email = ? OR phone = ? OR id_number = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ps.setString(2, phone);
            ps.setString(3, idNumber);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public static synchronized String generateNewAccountNumber() {
        return "FS" + String.format("%08d", 
            System.currentTimeMillis() % 100_000_000L);
    }

    private static void verifyDatabaseStructure() {
        try (Connection conn = getConnection();
             ResultSet rs = conn.getMetaData().getTables(null, null, "users", null)) {
            
            if (!rs.next()) {
                throw new IllegalStateException("Database tables not initialized. Run schema script first!");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database verification failed", e);
        }
    }

    // Transaction methods
    public static boolean logTransaction(String accountNumber, String type, 
                                       double amount, double balanceAfter) throws SQLException {
        String sql = "INSERT INTO transactions (account_number, transaction_type, " +
                   "amount, balance_after) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, accountNumber);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setDouble(4, balanceAfter);
            
            return ps.executeUpdate() > 0;
        }
    }

    // Transaction-safe balance update
    public static boolean updateBalance(String accountNumber, double amount) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            
            // Update balance
            String updateSql = "UPDATE users SET balance = balance + ? WHERE account_number = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setDouble(1, amount);
                ps.setString(2, accountNumber);
                ps.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    public static boolean accountExists(String accountNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE account_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public static boolean transferCash(String sender, String recipient, double amount) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Withdraw from sender
            updateBalance(sender, -amount);
            
            // Deposit to recipient
            updateBalance(recipient, amount);
            
            // Get balances after transaction
            double senderBalance = getBalance(sender);
            double recipientBalance = getBalance(recipient);
            
            // Log transactions
            logTransaction(sender, "Cash Send", -amount, senderBalance);
            logTransaction(recipient, "Cash Send", amount, recipientBalance);
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    public static boolean updatePin(String accountNumber, String newPin) throws SQLException {
    final String sql = "UPDATE users SET pin_hash = ? WHERE account_number = ?";
    
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, SecurityUtil.hashPassword(newPin));
        ps.setString(2, accountNumber);
        
        return ps.executeUpdate() > 0;
    }
}

public static boolean validateCurrentPin(String accountNumber, String currentPin) throws SQLException {
    final String sql = "SELECT pin_hash FROM users WHERE account_number = ?";
    
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, accountNumber);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String storedHash = rs.getString("pin_hash");
                return SecurityUtil.hashPassword(currentPin).equals(storedHash);
            }
        }
    }
    return false;
}

public static List<String> getRecentTransactions(String accountNumber, int limit) throws SQLException {
    List<String> transactions = new ArrayList<>();
    String sql = "SELECT transaction_date, transaction_type, amount, balance_after "
               + "FROM transactions "
               + "WHERE account_number = ? "
               + "ORDER BY transaction_date DESC "
               + "LIMIT ?";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, accountNumber);
        ps.setInt(2, limit);
        
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String date = rs.getTimestamp("transaction_date").toLocalDateTime().toLocalDate().toString();
                String type = rs.getString("transaction_type");
                double amount = rs.getDouble("amount");
                double balance = rs.getDouble("balance_after");
                
                String formatted = String.format("%-12s %-15s %-10s %-15s",
                    date,
                    type,
                    String.format("%,.2f", amount),
                    String.format("%,.2f", balance));
                
                transactions.add(formatted);
            }
        }
    }
    return transactions;
}


    public static double getBalance(String accountNumber) throws SQLException {
        final String sql = "SELECT balance FROM users WHERE account_number = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("balance") : -1;
            }
        }
    }
    
}
