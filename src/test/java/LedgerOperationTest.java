package com.protocol;

import com.protocol.audit.AuditLogger;
import com.protocol.ledger.BankingEngine;
import com.protocol.trace.StateTraceExtension;
import com.protocol.trace.StateTracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(StateTraceExtension.class)
public class LedgerOperationTest {

    private BankingEngine engine;

    @BeforeEach
    void setUp() {
        engine = new BankingEngine(new AuditLogger());
    }

    // --- Deposit ---

    @Test
    void deposit_shouldIncreaseBalance() {
        BigDecimal before = engine.getBalance("ACC-001");
        engine.deposit("ACC-001", new BigDecimal("250.00"));
        BigDecimal after = engine.getBalance("ACC-001");

        StateTracer.logTransition("ACC-001", before, "DEPOSIT", new BigDecimal("250.00"), after);
        StateTracer.logExpected(new BigDecimal("250.00"), after);

        assertEquals(0, after.compareTo(new BigDecimal("250.00")));
    }

    @Test
    void deposit_multipleDeposits_shouldAccumulate() {
        BigDecimal before = engine.getBalance("ACC-001");
        engine.deposit("ACC-001", new BigDecimal("100.00"));
        BigDecimal mid = engine.getBalance("ACC-001");
        engine.deposit("ACC-001", new BigDecimal("50.75"));
        BigDecimal after = engine.getBalance("ACC-001");

        StateTracer.logTransition("ACC-001", before, "DEPOSIT", new BigDecimal("100.00"), mid);
        StateTracer.logTransition("ACC-001", mid, "DEPOSIT", new BigDecimal("50.75"), after);
        StateTracer.logExpected(new BigDecimal("150.75"), after);

        assertEquals(0, after.compareTo(new BigDecimal("150.75")));
    }

    @Test
    void deposit_zeroAmount_shouldThrow() {
        StateTracer.logException("IllegalArgumentException", "DEPOSIT $0.00");
        assertThrows(IllegalArgumentException.class,
                () -> engine.deposit("ACC-001", BigDecimal.ZERO));
    }

    @Test
    void deposit_negativeAmount_shouldThrow() {
        StateTracer.logException("IllegalArgumentException", "DEPOSIT -$10.00");
        assertThrows(IllegalArgumentException.class,
                () -> engine.deposit("ACC-001", new BigDecimal("-10.00")));
    }

    // --- Withdrawal ---

    @Test
    void withdraw_shouldDecreaseBalance() {
        engine.deposit("ACC-001", new BigDecimal("500.00"));
        BigDecimal before = engine.getBalance("ACC-001");
        engine.withdraw("ACC-001", new BigDecimal("200.00"));
        BigDecimal after = engine.getBalance("ACC-001");

        StateTracer.logTransition("ACC-001", before, "WITHDRAWAL", new BigDecimal("200.00"), after);
        StateTracer.logExpected(new BigDecimal("300.00"), after);

        assertEquals(0, after.compareTo(new BigDecimal("300.00")));
    }

    @Test
    void withdraw_exactBalance_shouldLeaveZero() {
        engine.deposit("ACC-001", new BigDecimal("100.00"));
        BigDecimal before = engine.getBalance("ACC-001");
        engine.withdraw("ACC-001", new BigDecimal("100.00"));
        BigDecimal after = engine.getBalance("ACC-001");

        StateTracer.logTransition("ACC-001", before, "WITHDRAWAL", new BigDecimal("100.00"), after);
        StateTracer.logExpected(BigDecimal.ZERO, after);

        assertEquals(0, after.compareTo(BigDecimal.ZERO));
    }

    @Test
    void withdraw_insufficientFunds_shouldThrow() {
        engine.deposit("ACC-001", new BigDecimal("50.00"));
        StateTracer.logException("IllegalStateException", "WITHDRAWAL $100.00 from $50.00 balance");
        assertThrows(IllegalStateException.class,
                () -> engine.withdraw("ACC-001", new BigDecimal("100.00")));
    }

    @Test
    void withdraw_fromNonExistentAccount_shouldThrow() {
        StateTracer.logException("IllegalStateException", "WITHDRAWAL from non-existent account GHOST");
        assertThrows(IllegalStateException.class,
                () -> engine.withdraw("GHOST", new BigDecimal("10.00")));
    }

    @Test
    void withdraw_negativeAmount_shouldThrow() {
        engine.deposit("ACC-001", new BigDecimal("100.00"));
        StateTracer.logException("IllegalArgumentException", "WITHDRAWAL -$10.00");
        assertThrows(IllegalArgumentException.class,
                () -> engine.withdraw("ACC-001", new BigDecimal("-10.00")));
    }

    // --- Transfer ---

    @Test
    void transfer_shouldMoveFundsBetweenAccounts() {
        engine.deposit("ACC-001", new BigDecimal("1000.00"));
        engine.deposit("ACC-002", new BigDecimal("200.00"));

        BigDecimal beforeFrom = engine.getBalance("ACC-001");
        BigDecimal beforeTo   = engine.getBalance("ACC-002");

        engine.transfer("ACC-001", "ACC-002", new BigDecimal("400.00"));

        BigDecimal afterFrom = engine.getBalance("ACC-001");
        BigDecimal afterTo   = engine.getBalance("ACC-002");

        StateTracer.logTransition("ACC-001", beforeFrom, "TRANSFER OUT", new BigDecimal("400.00"), afterFrom);
        StateTracer.logExpected(new BigDecimal("600.00"), afterFrom);
        StateTracer.logTransition("ACC-002", beforeTo, "TRANSFER IN", new BigDecimal("400.00"), afterTo);
        StateTracer.logExpected(new BigDecimal("600.00"), afterTo);

        assertEquals(0, afterFrom.compareTo(new BigDecimal("600.00")));
        assertEquals(0, afterTo.compareTo(new BigDecimal("600.00")));
    }

    @Test
    void transfer_insufficientFunds_shouldNotMutateAnyBalance() {
        engine.deposit("ACC-001", new BigDecimal("100.00"));
        engine.deposit("ACC-002", new BigDecimal("50.00"));

        StateTracer.logException("IllegalStateException", "TRANSFER $500.00 from $100.00 balance");

        assertThrows(IllegalStateException.class,
                () -> engine.transfer("ACC-001", "ACC-002", new BigDecimal("500.00")));

        BigDecimal afterFrom = engine.getBalance("ACC-001");
        BigDecimal afterTo   = engine.getBalance("ACC-002");

        StateTracer.logExpected(new BigDecimal("100.00"), afterFrom);
        StateTracer.logExpected(new BigDecimal("50.00"), afterTo);

        assertEquals(0, afterFrom.compareTo(new BigDecimal("100.00")));
        assertEquals(0, afterTo.compareTo(new BigDecimal("50.00")));
    }

    @Test
    void transfer_zeroAmount_shouldThrow() {
        engine.deposit("ACC-001", new BigDecimal("100.00"));
        StateTracer.logException("IllegalArgumentException", "TRANSFER $0.00");
        assertThrows(IllegalArgumentException.class,
                () -> engine.transfer("ACC-001", "ACC-002", BigDecimal.ZERO));
    }

    // --- Balance ---

    @Test
    void getBalance_nonExistentAccount_shouldReturnZero() {
        BigDecimal balance = engine.getBalance("UNKNOWN");
        StateTracer.logExpected(BigDecimal.ZERO, balance);
        assertEquals(0, balance.compareTo(BigDecimal.ZERO));
    }
}