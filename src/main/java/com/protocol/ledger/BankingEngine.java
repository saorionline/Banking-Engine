package com.protocol.ledger;
import com.protocol.audit.AuditLogger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;


public class BankingEngine {
    private final Map<String, Double> accounts = new HashMap<>();
    private final List<String> auditLog = new ArrayList<>();

    // "Trigger" - Simulated automated audit logging
    private void executeAuditTrigger(String action, String accountId, double amount) {
        auditLog.add(String.format("AUDIT: %s | Account: %s | Amount: %.2f", action, accountId, amount));
    }

    // "Procedure" - Deposit logic with validation
    public void deposit(String accountId, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Invalid deposit amount");
        accounts.put(accountId, accounts.getOrDefault(accountId, 0.0) + amount);
        executeAuditTrigger("DEPOSIT", accountId, amount);
    }

    // "Procedure" - Withdrawal logic with balance validation
    public void withdraw(String accountId, double amount) {
        double currentBalance = accounts.getOrDefault(accountId, 0.0);
        if (amount > currentBalance) throw new IllegalStateException("Insufficient funds");
        
        accounts.put(accountId, currentBalance - amount);
        executeAuditTrigger("WITHDRAWAL", accountId, amount);
    }

    // "Procedure" - Atomic Transfer
    public void transfer(String fromId, String toId, double amount) {
        withdraw(fromId, amount);
        deposit(toId, amount);
        executeAuditTrigger("TRANSFER", fromId + " to " + toId, amount);
    }

    public double getBalance(String accountId) {
        return accounts.getOrDefault(accountId, 0.0);
    }

    public List<String> getAuditLog() {
        return new ArrayList<>(auditLog);
    }
}