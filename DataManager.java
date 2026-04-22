import java.io.*;
import java.time.LocalDate;

// OOP Concept: Utility class for File I/O persistence
// Saves and loads all student data so history survives across sessions
public class DataManager {

    // ----------------------------------------------------------------
    // SAVE: writes all student data to the save file
    // Format per line: TYPE|field1|field2|...
    // ----------------------------------------------------------------
    public static void saveData(Student student) {
        String fileName = student.getName() + "_expense_data.txt";
        // OOP Concept: Exception Handling (try-with-resources for automatic close)
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {

            // Line 1: student profile
            writer.println("PROFILE|" + student.getName()
                    + "|" + student.getPassword()
                    + "|" + student.getMonthlyBudget());

            // One line per expense — includes the original date for monthly tracking
            for (Expense e : student.getExpenses()) {
                writer.println("EXPENSE|" + e.getDate()
                        + "|" + e.getCategory()
                        + "|" + e.getAmount()
                        + "|" + e.getDescription());
            }

            // Recurring expense templates
            for (RecurringExpense r : student.getRecurringExpenses()) {
                writer.println("RECURRING|" + r.getCategory()
                        + "|" + r.getAmount()
                        + "|" + r.getDayOfMonth()
                        + "|" + r.getDescription());
            }

            // Savings goal (if any)
            if (student.getGoal() != null) {
                SavingsGoal g = student.getGoal();
                writer.println("GOAL|" + g.getGoalName()
                        + "|" + g.getTargetAmount()
                        + "|" + g.getSavedAmount());
            }

            System.out.println("✓ All data saved to \"" + fileName + "\".");

        } catch (IOException e) {
            System.out.println("⚠ Error saving data: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // LOAD: reads the save file and reconstructs the Student object
    // Returns null if the file does not exist or cannot be parsed
    // ----------------------------------------------------------------
    public static Student loadData(String name) {
        String fileName = name + "_expense_data.txt";
        File file = new File(fileName);
        if (!file.exists()) return null;

        Student student = null;

        // OOP Concept: Exception Handling (try-with-resources)
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Split on pipe character, keep trailing empty strings
                String[] parts = line.split("\\|", -1);

                switch (parts[0]) {

                    case "PROFILE":
                        String loadedName     = parts[1];
                        String loadedPassword = parts[2];
                        double loadedBudget   = Double.parseDouble(parts[3]);
                        student = new Student(loadedName, loadedBudget, loadedPassword);
                        break;

                    case "EXPENSE":
                        if (student != null) {
                            // Restore original date so monthly grouping stays correct
                            LocalDate date   = LocalDate.parse(parts[1]);
                            Category  cat    = Category.valueOf(parts[2]);
                            double    amount = Double.parseDouble(parts[3]);
                            String    desc   = parts[4];
                            student.addExpenseWithDate(amount, cat, desc, date);
                        }
                        break;

                    case "RECURRING":
                        if (student != null) {
                            Category rCat   = Category.valueOf(parts[1]);
                            double   rAmt   = Double.parseDouble(parts[2]);
                            int      rDay   = Integer.parseInt(parts[3]);
                            String   rDesc  = parts[4];
                            // Silent: no "template created" message on every startup
                            student.addRecurringExpenseSilent(rAmt, rCat, rDesc, rDay);
                        }
                        break;

                    case "GOAL":
                        if (student != null) {
                            String goalName  = parts[1];
                            double target    = Double.parseDouble(parts[2]);
                            double saved     = Double.parseDouble(parts[3]);
                            student.setGoalFromDB(goalName, target, saved);
                        }
                        break;

                    default:
                        // Unknown line type — skip silently
                        break;
                }
            }

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("⚠ Error loading data: " + e.getMessage());
            return null;
        }

        return student;
    }

    // Checks whether a previously saved session file exists
    public static boolean saveFileExists(String name) {
        String fileName = name + "_expense_data.txt";
        return new File(fileName).exists();
    }
}
