# ATM Banking System 💰

A Java Swing-based ATM simulation system integrated with MySQL for secure banking operations.


---

## Features ✨

- 🔐 User authentication with SHA-256 password hashing  
- 🧾 Account creation wizard  
- 💸 Deposit / Withdrawal / Transfers  
- 💰 Balance inquiry  
- 🔄 PIN management  
- 📃 Mini-statements  
- 🔒 Secure database interactions using parameterized queries

---

## Prerequisites 📋

- Java JDK 17+
- MySQL Server 8.0+
- Maven 3.8+
- MySQL Connector/J 8.0.23+

---

## Installation 🛠️

### 1. Clone Repository

```bash
git clone https://github.com/kingh66/atm-banking-system.git
cd atm-banking-system
```

### 2. Database Setup

```sql
CREATE DATABASE banking_system;
USE banking_system;

-- Users Table
CREATE TABLE users (
    account_number VARCHAR(20) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    id_number VARCHAR(20) UNIQUE NOT NULL,
    dob DATE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    gender ENUM('Male','Female','Other') NOT NULL,
    address VARCHAR(255) NOT NULL,
    nationality VARCHAR(50) NOT NULL,
    account_type ENUM('Savings','Current') NOT NULL,
    services SET('Online Banking','Mobile Banking','Cheque Book','SMS Alerts'),
    pin_hash CHAR(64) NOT NULL,
    password_hash CHAR(64) NOT NULL,
    balance DECIMAL(15,2) UNSIGNED DEFAULT 0.00
);

-- Transactions Table
CREATE TABLE transactions (
    transaction_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    transaction_type ENUM('Deposit','Withdrawal','Cash Send','Balance Inquiry','PIN Change') NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    balance_after DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (account_number) REFERENCES users(account_number)
);
```

### 3. Configure Database Connection

Update your `DatabaseManager.java` with your MySQL credentials:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_system";
private static final String DB_USER = "";
private static final String DB_PASSWORD = "";
```

### 4. Build & Run the Project

```bash
mvn clean install
java -cp target/atm-system-1.0.jar atm_system.Login
```

---

## Usage 🖥️

### Demo Login Credentials

```txt
Account Number: FS12345678  
Password: Test@1234
```

### Transaction Buttons and Actions

| Button       | Action                       |
|--------------|------------------------------|
| DEPOSIT      | Add funds to account         |
| WITHDRAWAL   | Withdraw cash                |
| CASH SEND    | Transfer to another account  |
| STATEMENT    | View transaction history     |
| CHANGE PIN   | Update security PIN          |
| BALANCE INQUIRY | Check current balance    |

---

## Security 🔒

- SHA-256 password hashing  
- Parameterized SQL queries  
- Input validation  
- Session management  
- Sensitive data masking

---

## Directory Structure 📂

```
atm-system/
├── src/
│   ├── main/
│   │   ├── java/atm_system/
│   │   │   ├── Login.java
│   │   │   ├── Signup.java
│   │   │   ├── SignupPage2.java
│   │   │   ├── AccountSummary.java
│   │   │   ├── Dashboard.java
│   │   │   └── ...
│   │   └── resources/
│   │       └── Icons/
├── pom.xml
└── README.md
```

---

## Dependencies 🛆

```xml
<dependencies>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.23</version>
    </dependency>
    <dependency>
        <groupId>com.toedter</groupId>
        <artifactId>jcalendar</artifactId>
        <version>1.4</version>
    </dependency>
</dependencies>
```

---

## Troubleshooting 🐛

### Database Connection Failed

- Ensure MySQL service is running  
- Check firewall or port blockages  
- Double-check DB credentials in `DatabaseManager.java`

### Missing Icons

- Ensure all required icons are in `src/main/resources/Icons/`  
- Supported formats: JPG, PNG

---

## License 📄

MIT License - See [LICENSE](LICENSE) for full details.

---

## Contributors 👥

- Sizwe Mthembu - Initial development

