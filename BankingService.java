import java.util.ArrayList;
import java.util.List;

// 1. This represents your 'accounts' table
class Account {
    int id;
    String owner;
    double balance;

    public Account(int id, String owner, double balance) {
        this.id = id;
        this.owner = owner;
        this.balance = balance;
    }
}

public class BankingService {
    // This represents your 'transaction_log' table
    private static List<String> auditLog = new ArrayList<>();

    public static void main(String[] args) {
        // Mockup data (Like your seed_data.sql)
        Account myAccount = new Account(1, "Sao", 1000.00);

        System.out.println("--- Initial State ---");
        System.out.println("Owner: " + myAccount.owner + " | Balance: $" + myAccount.balance);

        // Execute the logic
        depositMoney(myAccount, 500.00);

        System.out.println("\n--- Audit Log Results (The Trigger Simulator) ---");
        for (String log : auditLog) {
            System.out.println(log);
        }
    }

    /**
     * This method is the Java version of your PL/SQL PROCEDURE: deposit_money
     */
    public static void depositMoney(Account account, double amount) {
        double oldBalance = account.balance;
        
        // Update logic
        account.balance += amount;
        
        System.out.println("\n[JAVA LOGIC]: Deposit successful. New balance: $" + account.balance);

        // 2. This part simulates your SQL TRIGGER: audit_balance_change
        // In SQL, the trigger happens automatically. In Java, we call it manually.
        executeTriggerSimulation(account.id, oldBalance, account.balance);
    }

    /**
     * This simulates the AUTOMATION of your SQL Trigger
     */
    private static void executeTriggerSimulation(int accountId, double oldBal, double newBal) {
        double difference = newBal - oldBal;
        String logEntry = "AUDIT: Account " + accountId + " changed by " + difference + ".";
        auditLog.add(logEntry);
    }
}