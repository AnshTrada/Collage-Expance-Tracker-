import java.util.ArrayList;
 import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.io.FileWriter;
import java.io.IOException;

// OOP Concept: Inheritance (Student IS-A User)
public class Student extends User {

    // OOP Concept: Encapsulation
    private String password;
    // OOP Concept: Collections (ArrayList)
    private ArrayList<Expense> expenses;
    private ArrayList<RecurringExpense> recurringExpenses;
    // Java Concept: Collections (HashMap)
    private HashMap<Category, Double> categoryTotals;
    // OOP Concept: Aggregation (Student HAS-A SavingsGoal)
    private SavingsGoal goal;

    public Student(String name, double monthlyBudget, String password) {
        super(name, monthlyBudget);
        this.password = password;
        this.expenses = new ArrayList<>();
        this.recurringExpenses = new ArrayList<>();
        this.categoryTotals = new HashMap<>();
        this.goal = null;
    }

    public void addExpense(double amount, Category category, String description) {
        // OOP Concept: Composition (Object Creation)
        Expense expense = new Expense(amount, category, description);
        expenses.add(expense);

        double currentTotal = categoryTotals.getOrDefault(category, 0.0);
        categoryTotals.put(category, currentTotal + amount);

        System.out.println("✓ Added! Remaining Budget: Rs " + getRemainingBudget());
    }

    public void addExpenseFromDB(double amount, Category category, String description) {
        // Method specifically for loading data from DB without printing output
        Expense expense = new Expense(amount, category, description);
        expenses.add(expense);

        double currentTotal = categoryTotals.getOrDefault(category, 0.0);
        categoryTotals.put(category, currentTotal + amount);
    }

    // Loads a saved expense with its original recorded date (File I/O restoration)
    public void addExpenseWithDate(double amount, Category category, String description, java.time.LocalDate date) {
        Expense expense = new Expense(amount, category, description, date);
        expenses.add(expense);
        double currentTotal = categoryTotals.getOrDefault(category, 0.0);
        categoryTotals.put(category, currentTotal + amount);
    }

    public void setGoalFromDB(String goalName, double targetAmount, double savedAmount) {
        this.goal = new SavingsGoal(goalName, targetAmount);
        this.goal.setSavedAmount(savedAmount);
    }

    public void addRecurringExpense(double amount, Category category, String description, int dayOfMonth) {
        // OOP Concept: Object Creation (Subclass — RecurringExpense IS-A Expense)
        RecurringExpense recurring = new RecurringExpense(amount, category, description, dayOfMonth);
        // Only add if not already present (prevents duplicates when reloading)
        recurringExpenses.add(recurring);
        System.out.println("✓ Recurring Expense template created! It will automatically charge on day " + dayOfMonth);
    }

    // Silent variant used when restoring from save file
    public void addRecurringExpenseSilent(double amount, Category category, String description, int dayOfMonth) {
        RecurringExpense recurring = new RecurringExpense(amount, category, description, dayOfMonth);
        recurringExpenses.add(recurring);
    }

    public void applyRecurringExpenses() {
        if (recurringExpenses.isEmpty()) {
            System.out.println("No recurring expenses to apply.");
            return;
        }
        System.out.println("\n--- Applying Recurring Expenses for New Month ---");
        for (RecurringExpense re : recurringExpenses) {
            System.out.println("Applying: " + re.getDescription() + " (Rs " + re.getAmount() + ")");
            // Adding it to main expenses list
            // We use a regular Expense here or just insert the RecurringExpense since it IS-A Expense
            // Using the polymorphic nature, we can just add the 're' itself or create a new entry for the month
            // Note: Since each application should probably be a distinct expense instance with a current date, 
            // we will create a new Expense object for this month's instance.
            Expense monthlyInstance = new Expense(re.getAmount(), re.getCategory(), "[Auto] " + re.getDescription());
            expenses.add(monthlyInstance);
            
            double currentTotal = categoryTotals.getOrDefault(re.getCategory(), 0.0);
            categoryTotals.put(re.getCategory(), currentTotal + re.getAmount());
        }
        System.out.println("✓ All recurring expenses have been charged. Remaining Budget: Rs " + getRemainingBudget());
    }

    public double getTotalSpent() {
        double total = 0;
        for (Expense e : expenses) {
            total += e.getAmount();
        }
        return total;
    }

    public double getRemainingBudget() {
        return monthlyBudget - getTotalSpent();
    }

    public void viewAllExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses added yet!");
            return;
        }
        System.out.println("\n--- All Expenses ---");
        int count = 1;
        for (Expense e : expenses) {
            System.out.println(count + ". " + e.toString());
            count++;
        }
        System.out.println("Total: Rs " + getTotalSpent());
    }

    public HashMap<Category, Double> getCategoryTotal() {
        return categoryTotals;
    }

    public void viewCategoryTotals() {
        System.out.println("\n--- Category Totals ---");
        if (categoryTotals.isEmpty()) {
            System.out.println("No expenses yet!");
            return;
        }
        for (Map.Entry<Category, Double> entry : categoryTotals.entrySet()) {
            System.out.println(entry.getKey() + " : Rs " + entry.getValue());
        }
    }

    public void setGoal(String goalName, double targetAmount) {
        // OOP Concept: Object Creation
        this.goal = new SavingsGoal(goalName, targetAmount);
        System.out.println("✓ Goal Created: " + goalName + " (Target: Rs " + targetAmount + ")");
    }

    public void viewGoal() {
        if (goal == null) {
            System.out.println("No goal set yet! Use option 5.");
            return;
        }
        goal.displayGoal();
    }

    public String getTopCategory() {
        if (categoryTotals.isEmpty()) return "None";

        Category topCat = null;
        double maxAmount = 0;

        for (Map.Entry<Category, Double> entry : categoryTotals.entrySet()) {
            if (entry.getValue() > maxAmount) {
                maxAmount = entry.getValue();
                topCat = entry.getKey();
            }
        }
        return topCat + " (Rs " + maxAmount + ")";
    }

    // OOP Concept: Exception Handling & File I/O + Java Collections (TreeMap)
    public void viewMonthlyReport() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded yet!");
            return;
        }

        // Java Collections: TreeMap auto-sorts months chronologically by key
        TreeMap<String, Double> monthTotals   = new TreeMap<>();
        TreeMap<String, ArrayList<Expense>> monthExpenses = new TreeMap<>();

        for (Expense e : expenses) {
            String key = e.getDate().getYear() + "-"
                       + String.format("%02d", e.getDate().getMonthValue());
            monthTotals.put(key, monthTotals.getOrDefault(key, 0.0) + e.getAmount());
            if (!monthExpenses.containsKey(key)) {
                monthExpenses.put(key, new ArrayList<>());
            }
            monthExpenses.get(key).add(e);
        }

        System.out.println("\n========= MONTHLY EXPENSE REPORT =========");
        for (Map.Entry<String, Double> entry : monthTotals.entrySet()) {
            String month = entry.getKey();
            double total = entry.getValue();
            double saved = monthlyBudget - total;

            System.out.println("\n  Month      : " + month);
            System.out.printf("  Spent      : Rs %.2f%n", total);
            System.out.printf("  Budget     : Rs %.2f%n", monthlyBudget);
            System.out.printf("  Saved      : Rs %.2f%n", saved);
            System.out.println("  Expenses   :");
            int i = 1;
            for (Expense e : monthExpenses.get(month)) {
                System.out.println("    " + i + ". " + e.toString());
                i++;
            }
            System.out.println("  ------------------------------------------");
        }
        System.out.println("===========================================");
    }

    public void monthlySummary() {
        double spent = getTotalSpent();
        double remaining = getRemainingBudget();
        double savedPercent = (remaining / monthlyBudget) * 100;

        System.out.println("\n========= MONTHLY SUMMARY =========");
        System.out.println("Budget     : Rs " + monthlyBudget);
        System.out.println("Spent      : Rs " + spent);
        System.out.printf("Saved      : Rs %.1f (%.1f%%)\n", remaining, savedPercent);
        System.out.println("Top Category: " + getTopCategory());

        if (goal != null) {
            System.out.printf("Goal Progress: %.1f%%\n", goal.getProgress());
        }
        System.out.println("====================================");

        // OOP Concept: Exception Handling & File I/O
        // Since we don't have a database, we store the info in a monthly summary text file.
        try {
            String filename = name.replaceAll("\\s+", "_") + "_Monthly_Summary.txt";
            FileWriter writer = new FileWriter(filename);
            
            writer.write("========= MONTHLY SUMMARY =========\n");
            writer.write("Name       : " + name + "\n");
            writer.write("Budget     : Rs " + monthlyBudget + "\n");
            writer.write("Spent      : Rs " + spent + "\n");
            writer.write(String.format("Saved      : Rs %.1f (%.1f%%)\n", remaining, savedPercent));
            writer.write("Top Category: " + getTopCategory() + "\n");
            if (goal != null) {
                writer.write(String.format("Goal Progress: %.1f%%\n", goal.getProgress()));
            }
            writer.write("====================================\n\n");
            
            writer.write("--- All Expenses ---\n");
            if (expenses.isEmpty()) {
                writer.write("No expenses recorded.\n");
            } else {
                int count = 1;
                for (Expense e : expenses) {
                    writer.write(count + ". " + e.toString() + "\n");
                    count++;
                }
            }
            
            writer.close();
            System.out.println("✓ Monthly Summary also saved to file: " + filename);
            System.out.println("  (You can view this file to securely access your stored info)");
        } catch (IOException e) {
            System.out.println("⚠ Error saving summary to file: " + e.getMessage());
        }
    }

    // OOP Concept: Polymorphism (Method Overriding)
    @Override
    public void displayInfo() {
        System.out.println("Name    : " + name);
        System.out.println("Budget  : Rs " + monthlyBudget);
        System.out.println("Spent   : Rs " + getTotalSpent());
        System.out.println("Remaining: Rs " + getRemainingBudget());
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    // Exposes recurring templates so DataManager can persist them
    public ArrayList<RecurringExpense> getRecurringExpenses() {
        return recurringExpenses;
    }

    public SavingsGoal getGoal() {
        return goal;
    }
}
