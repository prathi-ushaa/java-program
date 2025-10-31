import java.sql.*;
import java.util.Scanner;

public class BankingSystem {

    // Database connection details
    static final String URL = "jdbc:mysql://localhost:3306/banking_system";
    static final String USER = "root";       // change if needed
    static final String PASSWORD = "root"; // change to your MySQL password

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("✅ Connected to Database!");

            while (true) {
                System.out.println("\n--- Simple Banking System ---");
                System.out.println("1. Create Account");
                System.out.println("2. Deposit Money");
                System.out.println("3. Withdraw Money");
                System.out.println("4. Check Balance");
                System.out.println("5. Exit");
                System.out.print("Enter choice: ");

                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        createAccount(conn, sc);
                        break;
                    case 2:
                        deposit(conn, sc);
                        break;
                    case 3:
                        withdraw(conn, sc);
                        break;
                    case 4:
                        checkBalance(conn, sc);
                        break;
                    case 5:
                        System.out.println("👋 Exiting... Thank you!");
                        return;
                    default:
                        System.out.println("❌ Invalid choice!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        sc.close();
    }

    // 1. Create Account
    private static void createAccount(Connection conn, Scanner sc) throws SQLException {
        sc.nextLine(); // clear buffer
        System.out.print("Enter account holder name: ");
        String name = sc.nextLine();

        String sql = "INSERT INTO accounts (name, balance) VALUES (?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setDouble(2, 0.0);
        stmt.executeUpdate();

        System.out.println("✅ Account created successfully!");
    }

    // 2. Deposit Money
    private static void deposit(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter account ID: ");
        int id = sc.nextInt();
        System.out.print("Enter amount to deposit: ");
        double amount = sc.nextDouble();

        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setDouble(1, amount);
        stmt.setInt(2, id);

        int rows = stmt.executeUpdate();
        if (rows > 0) {
            System.out.println("✅ Deposit successful!");
        } else {
            System.out.println("❌ Account not found.");
        }
    }

    // 3. Withdraw Money
    private static void withdraw(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter account ID: ");
        int id = sc.nextInt();
        System.out.print("Enter amount to withdraw: ");
        double amount = sc.nextDouble();

        String checkSql = "SELECT balance FROM accounts WHERE account_id = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
        checkStmt.setInt(1, id);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {
            double balance = rs.getDouble("balance");
            if (balance >= amount) {
                String sql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setDouble(1, amount);
                stmt.setInt(2, id);
                stmt.executeUpdate();
                System.out.println("✅ Withdrawal successful!");
            } else {
                System.out.println("❌ Insufficient balance!");
            }
        } else {
            System.out.println("❌ Account not found.");
        }
    }

    // 4. Check Balance
    private static void checkBalance(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter account ID: ");
        int id = sc.nextInt();

        String sql = "SELECT name, balance FROM accounts WHERE account_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            System.out.println("👤 Name: " + rs.getString("name"));
            System.out.println("💰 Balance: " + rs.getDouble("balance"));
        } else {
            System.out.println("❌ Account not found.");
        }
    }

}
#for data base table in mysql connector

    -- 1. Create the banking database
CREATE DATABASE IF NOT EXISTS banking_system;

-- 2. Use the database
USE banking_system;

-- 3. Create the accounts table
CREATE TABLE IF NOT EXISTS accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY, -- Unique ID for each account
    name VARCHAR(100) NOT NULL,                -- Account holder's name
    balance DOUBLE NOT NULL DEFAULT 0.0,       -- Account balance
    CHECK (balance >= 0)                       -- Prevent negative balances
);

-- Optional: Add a sample account
INSERT INTO accounts (name, balance)
VALUES ('John Doe', 1000.00);

-- Check the data
SELECT * FROM accounts;
