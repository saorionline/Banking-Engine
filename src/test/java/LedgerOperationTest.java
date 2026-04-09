package com.protocol;

import com.protocol.audit.AuditLogger;
import com.protocol.ledger.BankingEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class LedgerOperationTest {

    private BankingEngine engine;

    @BeforeEach
    void setUp() {
        engine = new BankingEngine(new AuditLogger());
    }

    // --- Deposit ---

    @Test
    void deposit_shouldIncreaseBalance() {
        engine.deposit("ACC-001", new BigDecimal("250.00"));
        assertEquals(0, engine.getBalance("ACC-001").compareTo(new BigDecimal("250.00")));
    }

    @Test
    void deposit_multipleDeposits_shouldAccumulate() {
        engine.deposit("ACC-001", new BigDecimal("100.00"));
        engine.deposit("ACC-001", new BigDecimal("50.75"));
        assertEquals(0, engine.getBalance("ACC-001").compareTo(new BigDecimal("150.75")));
    }

    @Test
    void deposit_zeroAmount_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> engine.deposit("ACC-001", BigDecimal.ZERO));
    }

    @Test
    void deposit_negativeAmount_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> engine.deposit("ACC-001", new BigDecimal("-10.00")));
    }

    // --- Withdrawal ---

    @Test
    void withdraw_shouldDecreaseBalance() {
        engine.deposit("ACC-001", new BigDecimal("500.00"));
        engine.withdraw("ACC-001", new BigDecimal("200.00"));
        assertEquals(0, engine.getBalance("ACC-001").compareTo(new BigDecimal("300.00")));
    }

    @Test
    void withdraw_exactBalance_shouldLeaveZero() {
        engine.deposit("ACC-001", new BigDecimal("100.00"));
        engine.withdraw("ACC-001", new BigDecimal("100.00"));
        assertEquals(0, engine.getBalance("ACC-001").compareTo(BigDecimal.ZERO));
    }

    @Test
    void withdraw_insufficientFunds_shouldThrow() {
        engine.deposit("ACC-001", new BigDecimal("50.00"));
        assertThrows(IllegalStateException.class,
                () -> engine.withdraw("ACC-001", new BigDecimal("100.00")));
    }

    @Test
    void withdraw_fromNonExistentAccount_shouldThrow() {
        assertThrows(IllegalStateException.class,
                () -> engine.withdraw("GHOST", new BigDecimal("10.00")));
    }

    @Test
    void withdraw_negativeAmount_shouldThrow() {
        engine.deposit("ACC-001", new BigDecimal("100.00"));
        assertThrows(IllegalArgumentException.class,
                () -> engine.withdraw("ACC-001", new BigDecimal("-10.00")));
    }

    // --- Transfer ---

    @Test
    void transfer_shouldMoveFundsBetweenAccounts() {
        engine.deposit("ACC-001", new BigDecimal("1000.00"));
        engine.deposit("ACC-002", new BigDecimal("200.00"));

        engine.transfer("ACC-001", "ACC-002", new BigDecimal("400.00"));

        assertEquals(0, engine.getBalance("ACC-001").compareTo(new BigDecimal("600.00")));
        assertEquals(0, engine.getBalance("ACC-002").compareTo(new BigDecimal("600.00")));
    }

    @Test
    void transfer_insufficientFunds_shouldNotMutateAnyBalance() {
        engine.deposit("ACC-001", new BigDecimal("100.00"));
        engine.deposit("ACC-002", new BigDecimal("50.00"));

        assertThrows(IllegalStateException.class,
                () -> engine.transfer("ACC-001", "ACC-002", new BigDecimal("500.00")));

        // Balances must be unchanged
        assertEquals(0, engine.getBalance("ACC-001").compareTo(new BigDecimal("100.00")));
        assertEquals(0, engine.getBalance("ACC-002").compareTo(new BigDecimal("50.00")));
    }

    @Test
    void transfer_zeroAmount_shouldThrow() {
        engine.deposit("ACC-001", new BigDecimal("100.00"));
        assertThrows(IllegalArgumentException.class,
                () -> engine.transfer("ACC-001", "ACC-002", BigDecimal.ZERO));
    }

    // --- Balance ---

    @Test
    void getBalance_nonExistentAccount_shouldReturnZero() {
        assertEquals(0,engine.getBalance("UNKNOWN").compareTo(BigDecimal.ZERO));
    }
}