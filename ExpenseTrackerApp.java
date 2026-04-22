import java.util.Scanner;

// OOP Concept: Main driver class (Entry Point)
public class ExpenseTrackerApp {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printBanner();

        // OOP Concept: File I/O — auto-restore previous session
        Student student = setupStudent();

        int choice = 0;

        while (choice != 8) {
            printMainMenu(student.getName());
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    handleAddExpense(student);
                    break;
                case 2:
                    // OOP Concept: Polymorphism (overridden toString on Expense objects)
                    student.viewAllExpenses();
                    break;
                case 3:
                    System.out.println("\n--- Budget Status ---");
                    // OOP Concept: Polymorphism (overridden displayInfo method)
                    student.displayInfo();
                    break;
                case 4:
                    // Java Collections: HashMap iteration
                    student.viewCategoryTotals();
                    break;
                case 5:
                    // OOP Concept: Inheritance (RecurringExpense IS-A Expense)
                    handleRecurringExpenses(student);
                    break;
                case 6:
                    // OOP Concept: Aggregation (Student HAS-A SavingsGoal)
                    handleSavingsGoal(student);
                    break;
                case 7:
                    // File I/O: generates monthly summary and saves to .txt file
                    student.monthlySummary();
                    break;
                case 8:
                    // Auto-save all data before exit — no data lost
                    DataManager.saveData(student);
                    System.out.println("Goodbye, " + student.getName() + "!");
                    break;
                default:
                    System.out.println("Invalid! Choose 1-8.");
            }
        }

        scanner.close();
    }

    // ----------------------------------------------------------------
    // Session Setup: auto-load saved data, or first-time profile setup
    // ----------------------------------------------------------------
    static Student setupStudent() {
        while (true) {
            System.out.println("\n--- Select User Type ---");
            System.out.println("1. Old User (Load Profile)");
            System.out.println("2. New User (Create Profile)");
            System.out.print("Choose: ");
            int userChoice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (userChoice == 1) {
                System.out.print("Enter your Name: ");
                String name = scanner.nextLine().trim();
                System.out.print("Enter your Password: ");
                String password = scanner.nextLine().trim();

                if (DataManager.saveFileExists(name)) {
                    Student loaded = DataManager.loadData(name);
                    if (loaded != null) {
                        if (loaded.getPassword().equals(password)) {
                            System.out.println("\n✓ Welcome back, " + loaded.getName() + "!");
                            System.out.println("  Your expenses and savings have been restored.\n");
                            return loaded;
                        } else {
                            System.out.println("Incorrect password! Please try again.\n");
                        }
                    } else {
                        System.out.println("Save file found but could not be read. Please try again or create a new profile.\n");
                    }
                } else {
                    System.out.println("No saved profile found for Name: " + name + ". Please try again.\n");
                }
            } else if (userChoice == 2) {
                System.out.println("\n--- Setup Profile ---");
                System.out.print("Full Name   : ");
                String name = scanner.nextLine().trim();

                System.out.print("Password    : ");
                String password = scanner.nextLine().trim();

                System.out.print("Monthly Budget (Rs): ");
                double budget = scanner.nextDouble();
                scanner.nextLine(); // consume newline

                // OOP Concept: Object Creation, Encapsulation, Inheritance
                Student student = new Student(name, budget, password);
                System.out.println("✓ Profile created successfully! Welcome, " + student.getName() + "!\n");
                return student;
            } else {
                System.out.println("Invalid choice. Please choose 1 or 2.");
            }
        }
    }

    // ----------------------------------------------------------------
    // Banner & Main Menu
    // ----------------------------------------------------------------
    static void printBanner() {
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║   COLLEGE EXPENSE TRACKER    ║");
        System.out.println("╚══════════════════════════════╝");
    }

    static void printMainMenu(String username) {
        System.out.println("\n======= MENU [" + username + "] =======");
        System.out.println("1. Add Expense");
        System.out.println("2. View All Expenses");
        System.out.println("3. View Budget Status");
        System.out.println("4. View Category Totals");
        System.out.println("5. Manage Recurring Expenses");
        System.out.println("6. Manage Savings Goal");
        System.out.println("7. Monthly Summary");
        System.out.println("8. Exit");
        System.out.println("================================");
        System.out.print("Choose: ");
    }

    // ----------------------------------------------------------------
    // Recurring Expenses Sub-Menu  (matches report Fig 4)
    // ----------------------------------------------------------------
    static void handleRecurringExpenses(Student student) {
        int subChoice = 0;

        while (subChoice != 3) {
            System.out.println("\n--- RECURRING EXPENSES ---");
            System.out.println("1. Add New Recurring Expense");
            System.out.println("2. Apply Pending Recurring Expenses (Monthly Charge)");
            System.out.println("3. Back");
            System.out.print("Choose: ");
            subChoice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (subChoice) {
                case 1:
                    handleAddRecurring(student);
                    break;
                case 2:
                    // OOP Concept: Polymorphism — iterates RecurringExpense IS-A Expense
                    student.applyRecurringExpenses();
                    break;
                case 3:
                    // back — loop exits
                    break;
                default:
                    System.out.println("Invalid! Choose 1-3.");
            }
        }
    }

    static void handleAddRecurring(Student student) {
        System.out.println("1=FOOD  2=TRANSPORT  3=BOOKS  4=ENTERTAINMENT");
        System.out.print("Category: ");
        int catChoice = scanner.nextInt();
        scanner.nextLine();
        Category cat = resolveCategory(catChoice);

        System.out.print("Amount (Rs): ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Charge Day of Month (1-31): ");
        int day = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Description: ");
        String desc = scanner.nextLine();

        student.addRecurringExpense(amount, cat, desc, day);
    }

    // ----------------------------------------------------------------
    // Savings Goal Sub-Menu  (matches report Fig 4)
    // ----------------------------------------------------------------
    static void handleSavingsGoal(Student student) {
        System.out.println("\n--- MANAGE SAVINGS GOAL ---");

        // If no goal exists yet — directly ask to create one
        if (student.getGoal() == null) {
            System.out.print("New Goal Name: ");
            String goalName = scanner.nextLine();
            System.out.print("Target Amount (Rs): ");
            double target = scanner.nextDouble();
            scanner.nextLine();
            student.setGoal(goalName, target);
            return;
        }

        // Goal already exists — show current status then sub-menu
        student.viewGoal();

        int subChoice = 0;
        while (subChoice != 3) {
            System.out.println("\n1. Add money to existing goal");
            System.out.println("2. Set a new goal (overwrite)");
            System.out.println("3. Back to menu");
            System.out.print("Choose: ");
            subChoice = scanner.nextInt();
            scanner.nextLine();

            switch (subChoice) {
                case 1:
                    System.out.print("Amount to add (Rs): ");
                    double amount = scanner.nextDouble();
                    scanner.nextLine();
                    if (amount <= 0) { System.out.println("Must be positive!"); break; }
                    student.getGoal().addSaving(amount);
                    break;
                case 2:
                    System.out.print("New Goal Name: ");
                    String goalName = scanner.nextLine();
                    System.out.print("Target Amount (Rs): ");
                    double target = scanner.nextDouble();
                    scanner.nextLine();
                    student.setGoal(goalName, target);
                    subChoice = 3; // exit sub-menu after setting
                    break;
                case 3:
                    break; // back
                default:
                    System.out.println("Invalid! Choose 1-3.");
            }
        }
    }

    // ----------------------------------------------------------------
    // Add Expense Handler  (matches report Fig 2)
    // ----------------------------------------------------------------
    static void handleAddExpense(Student student) {
        System.out.println("\n--- ADD EXPENSE ---");
        System.out.println("1=FOOD  2=TRANSPORT  3=BOOKS  4=ENTERTAINMENT");
        System.out.print("Category: ");
        int catChoice = scanner.nextInt();
        scanner.nextLine();

        // Java Concept: Enum usage
        Category selectedCategory = resolveCategory(catChoice);

        System.out.print("Amount (Rs): ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Description: ");
        String desc = scanner.nextLine();

        // OOP Concept: Exception Handling — budget safety check
        if (amount > student.getRemainingBudget()) {
            System.out.println("WARNING: This exceeds your remaining budget!");
            System.out.print("Still add? (y/n): ");
            if (!scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.println("Cancelled.");
                return;
            }
        }

        student.addExpense(amount, selectedCategory, desc);
    }

    // ----------------------------------------------------------------
    // Helper: resolve integer input to Category enum
    // ----------------------------------------------------------------
    static Category resolveCategory(int choice) {
        switch (choice) {
            case 1: return Category.FOOD;
            case 2: return Category.TRANSPORT;
            case 3: return Category.BOOKS;
            case 4: return Category.ENTERTAINMENT;
            default:
                System.out.println("Invalid category! Defaulting to FOOD.");
                return Category.FOOD;
        }
    }
}
