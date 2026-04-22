import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class DatabaseManager {

    // Default XAMPP / MySQL local credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "expense_tracker_db";
    private static final String USER = "root";
    private static final String PASS = "";

    private Connection connection;

    public DatabaseManager() {
        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to MySQL server (without specifying DB yet)
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            
            // Create database if not exists and switch to it
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            stmt.executeUpdate("USE " + DB_NAME);
            
            // Re-establish connection specifically to the database
            connection = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);

            createTables();
            System.out.println("[INFO] Connected to MySQL Database successfully!");

        } catch (ClassNotFoundException e) {
            System.out.println("✗ MySQL JDBC Driver not found. Did you add mysql-connector-j.jar to classpath?");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("✗ Could not connect to MySQL Database. Please ensure MySQL (e.g., XAMPP) is running.");
            e.printStackTrace();
        }
    }

    private void createTables() {
        try {
            Statement stmt = connection.createStatement();
            
            // Users table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR(50) PRIMARY KEY, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "roll_no VARCHAR(20), " +
                    "budget DOUBLE NOT NULL)";
            stmt.executeUpdate(createUsersTable);

            // Expenses table
            String createExpensesTable = "CREATE TABLE IF NOT EXISTS expenses (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50), " +
                    "amount DOUBLE NOT NULL, " +
                    "category VARCHAR(50) NOT NULL, " +
                    "description TEXT, " +
                    "expense_date VARCHAR(50) NOT NULL, " +
                    "FOREIGN KEY (username) REFERENCES users(username))";
            stmt.executeUpdate(createExpensesTable);

            // Savings Goals table
            String createGoalsTable = "CREATE TABLE IF NOT EXISTS savings_goals (" +
                    "username VARCHAR(50) PRIMARY KEY, " +
                    "goal_name VARCHAR(100) NOT NULL, " +
                    "target_amount DOUBLE NOT NULL, " +
                    "saved_amount DOUBLE DEFAULT 0, " +
                    "FOREIGN KEY (username) REFERENCES users(username))";
            stmt.executeUpdate(createGoalsTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- Users CRUD ---

    public boolean addUser(String username, String password, String name, String rollNo, double budget) {
        String sql = "INSERT INTO users (username, password, name, roll_no, budget) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.setString(4, rollNo);
            pstmt.setDouble(5, budget);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false; // Username already exists
        }
    }

    public boolean validateLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if a match is found
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Student getStudent(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Student student = new Student(
                    rs.getString("name"),
                    rs.getDouble("budget"),
                    rs.getString("roll_no")
                );
                
                // For a complete profile, we shouldn't wipe DB expenses yet, 
                // but student logic loads it separately or we inject it here.
                // It's cleaner to load expenses and goals here.
                
                return student;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT username FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Expenses CRUD ---

    public void addExpense(String username, Expense expense) {
        String sql = "INSERT INTO expenses (username, amount, category, description, expense_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setString(3, expense.getCategory().name());
            pstmt.setString(4, expense.getDescription());
            pstmt.setString(5, expense.getDate().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadStudentData(String username, Student student) {
        // 1. Load Expenses
        String sqlExp = "SELECT * FROM expenses WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlExp)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                double amount = rs.getDouble("amount");
                Category category = Category.valueOf(rs.getString("category"));
                String desc = rs.getString("description");
                
                // Internally add to student arraylist
                student.addExpenseFromDB(amount, category, desc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2. Load Goal
        String sqlGoal = "SELECT * FROM savings_goals WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlGoal)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String goalName = rs.getString("goal_name");
                double target = rs.getDouble("target_amount");
                double saved = rs.getDouble("saved_amount");
                
                student.setGoalFromDB(goalName, target, saved);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- Goals CRUD ---

    public void setOrUpdateGoal(String username, SavingsGoal goal) {
        // Using ON DUPLICATE KEY UPDATE for MySQL
        String sql = "INSERT INTO savings_goals (username, goal_name, target_amount, saved_amount) " +
                     "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                     "goal_name = VALUES(goal_name), target_amount = VALUES(target_amount), saved_amount = VALUES(saved_amount)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, goal.getGoalName());
            pstmt.setDouble(3, goal.getTargetAmount());
            pstmt.setDouble(4, goal.getSavedAmount());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTotalUsersCount() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
