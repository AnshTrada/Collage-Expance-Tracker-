import java.time.LocalDate;

public class Expense {

    // OOP Concept: Encapsulation
    private double amount;
    private Category category;
    private String description;
    private LocalDate date;

    public Expense(double amount, Category category, String description) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = LocalDate.now();
    }

    // Constructor used when loading saved data — restores the original expense date
    public Expense(double amount, Category category, String description, LocalDate date) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    // OOP Concept: Polymorphism (Method Overriding toString from Object class)
    @Override
    public String toString() {
        return "[" + category + "] Rs " + amount + " - " + description + " (" + date + ")";
    }
}
