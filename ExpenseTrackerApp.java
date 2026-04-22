import java.util.Scanner;

public class ExpenseTrackerApp {

    static Scanner scanner = new Scanner(System.in);
    
    // We create the authentication service here
    static AuthService authService = new AuthService();

    public static void main(String[] args) {
        printBanner();

        while (true) {
            if (!authService.isLoggedIn()) {
                System.out.println("\n--- MAIN MENU ---");
                System.out.println("1. Login");
                System.out.println("2. Register");
                System.out.println("3. Exit");
                System.out.print("Choose: ");
                
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                if (choice == 1) {
                    handleLogin();
                } else if (choice == 2) {
                    handleRegister();
                } else if (choice == 3) {
                    System.out.println("Exiting App...");
                    break;
                } else {
                    System.out.println("Invalid Option!");
                }
            } else {
                // Logged in!
                showUserDashboard();
            }
        }
        scanner.close();
    }
    
    static void handleLogin() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        
        authService.login(username, password);
    }
    
    static void handleRegister() {
        System.out.println("\n--- REGISTER ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Roll Number: ");
        String rollNo = scanner.nextLine().trim();
        System.out.print("Monthly Budget (Rs): ");
        double budget = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        
        authService.register(username, password, name, rollNo, budget);
    }

    static void showUserDashboard() {
        Student student = authService.getCurrentStudent();
        
        int choice = 0;
        while (choice != 7 && authService.isLoggedIn()) {
            printUserMenu(student.getName());
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    handleAddExpense(student);
                    break;
                case 2:
                    student.viewAllExpenses();
                    break;
                case 3:
                    System.out.println("\n--- Budget Status ---");
                    student.displayInfo();
                    break;
                case 4:
                    student.viewCategoryTotals();
                    break;
                case 5:
                    handleSetGoal(student);
                    break;
                case 6:
                    student.monthlySummary();
                    break;
                case 7:
                    authService.logout();
                    break;
                default:
                    System.out.println("Invalid! Choose 1-7.");
            }
        }
    }

    static void printBanner() {
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║   COLLEGE EXPENSE TRACKER    ║");
        System.out.println("╚══════════════════════════════╝");
    }

    static void printUserMenu(String username) {
        System.out.println("\n======= MENU [" + username + "] =======");
        System.out.println("1. Add Expense");
        System.out.println("2. View All Expenses");
        System.out.println("3. View Budget Status");
        System.out.println("4. View Category Totals");
        System.out.println("5. Set Savings Goal");
        System.out.println("6. Monthly Summary");
        System.out.println("7. Logout");
        System.out.println("================================");
        System.out.print("Choose: ");
    }

    static void handleAddExpense(Student student) {
        System.out.println("\n--- ADD EXPENSE ---");
        System.out.println("1=FOOD  2=TRANSPORT  3=BOOKS  4=ENTERTAINMENT");
        System.out.print("Category: ");
        int catChoice = scanner.nextInt();

        Category selectedCategory;
        switch (catChoice) {
            case 1:  selectedCategory = Category.FOOD;          break;
            case 2:  selectedCategory = Category.TRANSPORT;     break;
            case 3:  selectedCategory = Category.BOOKS;         break;
            case 4:  selectedCategory = Category.ENTERTAINMENT; break;
            default:
                System.out.println("Invalid! Setting FOOD.");
                selectedCategory = Category.FOOD;
        }

        System.out.print("Amount (Rs): ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Description: ");
        String desc = scanner.nextLine();

        if (amount > student.getRemainingBudget()) {
            System.out.println("⚠ WARNING: This exceeds your remaining budget!");
            System.out.print("Still add? (y/n): ");
            String confirm = scanner.nextLine();
            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("Cancelled.");
                return;
            }
        }

        // 1. Add it to memory
        Expense newExpense = student.addExpense(amount, selectedCategory, desc);
        // 2. Save it to Database
        authService.getDatabaseManager().addExpense(authService.getCurrentUsername(), newExpense);
    }

    static void handleSetGoal(Student student) {
        System.out.println("\n--- SET SAVINGS GOAL ---");

        System.out.print("Goal Name: ");
        String goalName = scanner.nextLine();

        System.out.print("Target Amount (Rs): ");
        double target = scanner.nextDouble();

        // 1. Set goal in memory
        SavingsGoal newGoal = student.setGoal(goalName, target);
        // 2. Save to database
        authService.getDatabaseManager().setOrUpdateGoal(authService.getCurrentUsername(), newGoal);
    }
}

