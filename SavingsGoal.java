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
    }

    public double getProgress() {
        if (targetAmount == 0) return 0;
        return (savedAmount / targetAmount) * 100;
    }

    public double getRemainingAmount() {
        return targetAmount - savedAmount;
    }

    public void displayGoal() {
        System.out.println("Goal     : " + goalName);
        System.out.println("Target   : Rs " + targetAmount);
        System.out.println("Saved    : Rs " + savedAmount);
        System.out.printf("Progress : %.1f%%\n", getProgress());
        System.out.println("Remaining: Rs " + getRemainingAmount());
        printProgressBar();
    }

    private void printProgressBar() {
        int filled = (int) (getProgress() / 5);
        System.out.print("Progress : [");
        for (int i = 0; i < 20; i++) {
            if (i < filled) {
                System.out.print("#");
            } else {
                System.out.print(".");
            }
        }
        System.out.println("]");
    }

    public String getGoalName() {
        return goalName;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public double getSavedAmount() {
        return savedAmount;
    }

    public void setSavedAmount(double savedAmount) {
        this.savedAmount = savedAmount;
    }
}
