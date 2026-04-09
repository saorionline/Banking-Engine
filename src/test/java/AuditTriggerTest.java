package com.protocol;

import com.protocol.audit.AuditLogger;
import com.protocol.ledger.BankingEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AuditTriggerTest {

    private AuditLogger auditLogger;
    private BankingEngine engine;

    @BeforeEach
    void setUp() {
        auditLogger = new AuditLogger();
        engine = new BankingEngine(auditLogger);
    }

    @Test
    void deposit_shouldCreateSingleAuditEntry() {
        engine.deposit("ACC-001", new BigDecimal("500.00"));

        List<String> log = auditLogger.getLog();
        assertEquals(1, log.size());
        assertTrue(log.get(0).contains("DEPOSIT"));
        assertTrue(log.get(0).contains("ACC-001"));
        assertTrue(log.get(0).contains("500.00"));
    }

    @Test
    void withdrawal_shouldCreateSingleAuditEntry() {
        engine.deposit("ACC-001", new BigDecimal("500.00"));
        auditLogger.clear(); // reset after setup deposit

        engine.withdraw("ACC-001", new BigDecimal("200.00"));

        List<String> log = auditLogger.getLog();
        assertEquals(1, log.size());
        assertTrue(log.get(0).contains("WITHDRAWAL"));
        assertTrue(log.get(0).contains("200.00"));
    }

    @Test
    void transfer_shouldCreateExactlyOneAuditEntry() {
        engine.deposit("ACC-001", new BigDecimal("1000.00"));
        auditLogger.clear(); // reset after setup deposit

        engine.transfer("ACC-001", "ACC-002", new BigDecimal("300.00"));

        List<String> log = auditLogger.getLog();
        // Must be exactly 1 TRANSFER entry — not 3 (withdraw + deposit + transfer)
        assertEquals(1, log.size());
        assertTrue(log.get(0).contains("TRANSFER"));
        assertTrue(log.get(0).contains("ACC-001"));
        assertTrue(log.get(0).contains("ACC-002"));
        assertTrue(log.get(0).contains("300.00"));
    }

    @Test
    void multipleOperations_shouldProduceOrderedAuditTrail() {
        engine.deposit("ACC-001", new BigDecimal("1000.00"));
        engine.deposit("ACC-002", new BigDecimal("500.00"));
        engine.withdraw("ACC-001", new BigDecimal("100.00"));

        List<String> log = auditLogger.getLog();
        assertEquals(3, log.size());
        assertTrue(log.get(0).contains("DEPOSIT"));
        assertTrue(log.get(1).contains("DEPOSIT"));
        assertTrue(log.get(2).contains("WITHDRAWAL"));
    }

    @Test
    void invalidDeposit_shouldNotCreateAuditEntry() {
        assertThrows(IllegalArgumentException.class,
                () -> engine.deposit("ACC-001", new BigDecimal("-50.00")));

        assertTrue(auditLogger.getLog().isEmpty());
    }

    @Test
    void insufficientFunds_shouldNotCreateAuditEntry() {
        engine.deposit("ACC-001", new BigDecimal("100.00"));
        auditLogger.clear();

        assertThrows(IllegalStateException.class,
                () -> engine.withdraw("ACC-001", new BigDecimal("999.00")));

        assertTrue(auditLogger.getLog().isEmpty());
    }
}