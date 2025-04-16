package atm_system;

public class User {
    private String accountNumber;
    private String fullName;
    private String idNumber;
    private String dob;
    private String email;
    private String phone;
    private String gender;
    private String address;
    private String nationality;
    private String accountType;
    private String services;
    private String pin;
    private String password;
    private double balance;

    public User(String accountNumber, String fullName, String idNumber, String dob,
                String email, String phone, String gender, String address,
                String nationality, String accountType, String services,
                String pin, String password, double balance) {
        this.accountNumber = accountNumber;
        this.fullName = fullName;
        this.idNumber = idNumber;
        this.dob = dob;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.address = address;
        this.nationality = nationality;
        this.accountType = accountType;
        this.services = services;
        this.pin = pin;
        this.password = password;
        this.balance = balance;
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getFullName() { return fullName; }
    public String getIdNumber() { return idNumber; }
    public String getDob() { return dob; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public String getNationality() { return nationality; }
    public String getAccountType() { return accountType; }
    public String getServices() { return services; }
    public String getPin() { return pin; }
    public String getPassword() { return password; }
    public double getBalance() { return balance; }

    // Setters
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public void setServices(String services) { this.services = services; }
    public void setPin(String pin) { this.pin = pin; }
    public void setPassword(String password) { this.password = password; }
    public void setBalance(double balance) { this.balance = balance; }

    // Optional: Add other setters if needed
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setDob(String dob) { this.dob = dob; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
}