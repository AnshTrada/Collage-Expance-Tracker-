public class RecurringExpense extends Expense {

    // Extra property for recurring logic
    private int dayOfMonth;

    public RecurringExpense(double amount, Category category, String description, int dayOfMonth) {
        // OOP Concept: Inheritance (calling superclass constructor)
        super(amount, category, description);
        this.dayOfMonth = dayOfMonth;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    // OOP Concept: Polymorphism (Method Overriding)
    @Override
    public String toString() {
        return super.toString() + " [Recurring every month on day " + dayOfMonth + "]";
    }
}
