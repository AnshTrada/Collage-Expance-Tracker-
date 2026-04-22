public class SavingsGoal {

    // OOP Concept: Encapsulation
    private String goalName;
    private double targetAmount;
    private double savedAmount;

    public SavingsGoal(String goalName, double targetAmount) {
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.savedAmount = 0;
    }

    public void addSaving(double amount) {
        savedAmount += amount;
        System.out.printf("✓ Rs %.2f added to savings! Total saved: Rs %.2f%n", amount, savedAmount);
    }

    public double getProgress() {
        if (targetAmount == 0) return 0;
        return (savedAmount / targetAmount) * 100;
    }

    public double getRemainingAmount() {
        double remaining = targetAmount - savedAmount;
        return remaining < 0 ? 0 : remaining;
    }

    public void displayGoal() {
        System.out.println("\n--- Savings Goal ---");
        System.out.println("Goal     : " + goalName);
        System.out.println("Target   : Rs " + targetAmount);
        System.out.println("Saved    : Rs " + savedAmount);
        System.out.printf("Progress : %.1f%%%n", Math.min(getProgress(), 100.0));
        System.out.println("Remaining: Rs " + getRemainingAmount());
        printProgressBar();

        if (savedAmount >= targetAmount) {
            System.out.println("🎉 Congratulations! Goal achieved!");
        }
    }

    private void printProgressBar() {
        double progress = Math.min(getProgress(), 100.0);
        int filled = (int) (progress / 5);
        System.out.print("Progress : [");
        for (int i = 0; i < 20; i++) {
            System.out.print(i < filled ? "#" : ".");
        }
        System.out.println("]");
    }

    // --- Getters ---
    public String getGoalName()      { return goalName; }
    public double getTargetAmount()  { return targetAmount; }
    public double getSavedAmount()   { return savedAmount; }

    // --- Setters (OOP Concept: Encapsulation - controlled mutation) ---
    public void setGoalName(String goalName)       { this.goalName = goalName; }
    public void setTargetAmount(double amount)      { this.targetAmount = amount; }
    public void setSavedAmount(double savedAmount)  { this.savedAmount = savedAmount; }
}
