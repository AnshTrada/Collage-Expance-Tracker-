import java.util.Scanner;

public class ExpenseTrackerApp {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printBanner();

        System.out.println("\n--- Setup Profile ---");
        System.out.print("Full Name   : ");
        String name = scanner.nextLine().trim();

        System.out.print("Roll Number : ");
        String rollNo = scanner.nextLine().trim();

        System.out.print("Monthly Budget (Rs): ");
        double budget = scanner.nextDouble();

        // OOP Concept: Object Creation, Encapsulation
        Student student = new Student(name, budget, rollNo);
        System.out.println("✓ Profile created successfully! Welcome, " + student.getName() + "!\n");

        int choice = 0;

        while (choice != 8) {
            printMainMenu(student.getName());
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    handleAddExpense(student);
                    break;
                case 2:
                    student.viewAllExpenses();
                    break;
                case 3:
                    System.out.println("\n--- Budget Status ---");
                    // OOP Concept: Polymorphism (Calling overridden method)
                    student.displayInfo();
                    break;
                case 4:
                    student.viewCategoryTotals();
                    break;
                case 5:
                    handleRecurring(student);
                    break;
                case 6:
                    handleSetGoal(student);
                    break;
                case 7:
                    student.monthlySummary();
                    break;
                case 8:
                    System.out.println("Goodbye, " + student.getName() + "!");
                    break;
                default:
                    System.out.println("Invalid! Choose 1-8.");
            }
        }

        scanner.close();
    }

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

        student.addExpense(amount, selectedCategory, desc);
    }

    static void handleSetGoal(Student student) {
        System.out.println("\n--- MANAGE SAVINGS GOAL ---");
        if (student.getGoal() != null) {
            student.viewGoal();
            System.out.println("\n1. Add money to existing goal");
            System.out.println("2. Set a new goal (overwrite)");
            System.out.println("3. Back to menu");
            System.out.print("Choose: ");
            int opt = scanner.nextInt();
            if (opt == 1) {
                System.out.print("Amount to add (Rs): ");
                double addAmt = scanner.nextDouble();
                student.getGoal().addSaving(addAmt);
                System.out.println("✓ Added Rs " + addAmt + " to savings!");
                return;
            } else if (opt == 3) {
                return;
            }
        }
        
        // This scanner.nextLine() clears the newline character from the buffer
        // left over from previous scanner.nextInt() or scanner.nextDouble()
        // so that the next scanner.nextLine() doesn't immediately read an empty string.
        scanner.nextLine();

        System.out.print("New Goal Name: ");
        String goalName = scanner.nextLine();

        System.out.print("Target Amount (Rs): ");
        double target = scanner.nextDouble();

        student.setGoal(goalName, target);
    }

    static void handleRecurring(Student student) {
        System.out.println("\n--- RECURRING EXPENSES ---");
        System.out.println("1. Add New Recurring Expense");
        System.out.println("2. Apply Pending Recurring Expenses (Monthly Charge)");
        System.out.println("3. Back");
        System.out.print("Choose: ");
        int opt = scanner.nextInt();
        
        if (opt == 1) {
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
            System.out.print("Charge Day of Month (1-31): ");
            int day = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Description: ");
            String desc = scanner.nextLine();
            student.addRecurringExpense(amount, selectedCategory, desc, day);
        } else if (opt == 2) {
            student.applyRecurringExpenses();
        }
    }
}
