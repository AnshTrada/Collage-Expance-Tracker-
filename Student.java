import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// OOP Concept: Inheritance (Student IS-A User)
public class Student extends User {

    // OOP Concept: Encapsulation
    private String rollNo;
    // OOP Concept: Collections (ArrayList)
    private ArrayList<Expense> expenses;
    private ArrayList<RecurringExpense> recurringExpenses;
    // Java Concept: Collections (HashMap)
    private HashMap<Category, Double> categoryTotals;
    // OOP Concept: Aggregation (Student HAS-A SavingsGoal)
    private SavingsGoal goal;

    public Student(String name, double monthlyBudget, String rollNo) {
        super(name, monthlyBudget);
        this.rollNo = rollNo;
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

    public void addRecurringExpense(double amount, Category category, String description, int dayOfMonth) {
        // OOP Concept: Object Creation (Subclass)
        RecurringExpense recurring = new RecurringExpense(amount, category, description, dayOfMonth);
        recurringExpenses.add(recurring);
        System.out.println("✓ Recurring Expense template created! It will automatically charge on day " + dayOfMonth);
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
    }

    // OOP Concept: Polymorphism (Method Overriding)
    @Override
    public void displayInfo() {
        System.out.println("Name    : " + name);
        System.out.println("Roll No : " + rollNo);
        System.out.println("Budget  : Rs " + monthlyBudget);
        System.out.println("Spent   : Rs " + getTotalSpent());
        System.out.println("Remaining: Rs " + getRemainingBudget());
    }

    public String getRollNo() {
        return rollNo;
    }

    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    public SavingsGoal getGoal() {
        return goal;
    }
}
