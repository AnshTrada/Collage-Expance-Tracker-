// OOP Concept: Abstraction

public abstract class User {

    // OOP Concept: Encapsulation
    protected String name;
    protected double monthlyBudget;

    public User(String name, double monthlyBudget) {
        this.name = name;
        this.monthlyBudget = monthlyBudget;
    }

    // OOP Concept: Abstraction (Abstract method)
    public abstract void displayInfo();

    public String getName() {
        return name;
    }

    public double getMonthlyBudget() {
        return monthlyBudget;
    }

    public void setMonthlyBudget(double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    public void setName(String name) {
        this.name = name;
    }
}
