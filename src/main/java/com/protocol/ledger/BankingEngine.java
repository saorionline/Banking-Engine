package com.protocol.ledger;

import com.protocol.audit.AuditLogger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;

public class BankingEngine {

    private final Map<String, BigDecimal> accounts = new ConcurrentHashMap<>();
    private final AuditLogger auditLogger;

    public BankingEngine(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    // "Procedure" - Deposit logic with validation
    public void deposit(String accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid deposit amount");
        }
        accounts.merge(accountId, amount, BigDecimal::add);
        auditLogger.log("DEPOSIT", accountId, format(amount));
    }

    // "Procedure" - Withdrawal logic with balance validation
    public void withdraw(String accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid withdrawal amount");
        }
        accounts.compute(accountId, (id, current) -> {
            BigDecimal balance = current != null ? current : BigDecimal.ZERO;
            if (amount.compareTo(balance) > 0) {
                throw new IllegalStateException("Insufficient funds");
            }
            return balance.subtract(amount);
        });
        auditLogger.log("WITHDRAWAL", accountId, format(amount));
    }

    // "Procedure" - Atomic Transfer (logged as a single TRANSFER event)
    public void transfer(String fromId, String toId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid transfer amount");
        }
        // Perform balance movement without triggering individual audit entries
        accounts.compute(fromId, (id, current) -> {
            BigDecimal balance = current != null ? current : BigDecimal.ZERO;
            if (amount.compareTo(balance) > 0) {
                throw new IllegalStateException("Insufficient funds");
            }
            return balance.subtract(amount);
        });
        accounts.merge(toId, amount, BigDecimal::add);

        // Single audit entry for the transfer
        auditLogger.log("TRANSFER", fromId + " -> " + toId, format(amount));
    }

    public BigDecimal getBalance(String accountId) {
        return accounts.getOrDefault(accountId, BigDecimal.ZERO);
    }

    public List<String> getAuditLog() {
        return auditLogger.getLog();
    }

    private String format(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}