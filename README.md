# 🏦 Banking Engine Simulation

A simulated database-level engine that models core banking operations using the **Repository Pattern**. Procedures and Triggers are implemented as synchronized methods and internal event listeners.

---

## 🗂️ Architecture Overview

```
BankingEngine (Mock Database)
├── 🗃️  State        → Account Balances (in-memory store)
├── ⚙️  Procedures   → deposit / withdraw / transfer
└── 🔔  Triggers     → Automated audit logging on every state change
```

---

## ⚙️ Core Procedures

| Procedure | Description | Validation |
|-----------|-------------|------------|
| `deposit` | Adds funds to an account | Amount must be > 0 |
| `withdraw` | Deducts funds from an account | Balance must be sufficient |
| `transfer` | Moves funds atomically between accounts | Combines withdraw + deposit |

---

## 🔔 Trigger Behavior

Every procedure internally fires an **Audit Trigger** — a private method that logs the operation automatically.

- ✅ No state change happens without a corresponding log entry
- ✅ The trigger is invisible to the caller — it fires inevitably
- ✅ Mirrors how database-level triggers work in production environments

---

## 🔒 Constraint Logic (Simulated Rollback)

The `withdraw` procedure validates balance **before** modifying state.
If validation fails, an exception is thrown and **no state change occurs** — simulating a SQL `CHECK` constraint and transaction rollback.

```
withdraw("ACC1", 9999)
  └── ❌ Insufficient funds
        └── State unchanged ✅
```

---

## 🧪 Unit Test Suite — Step by Step

### 🔧 Step 1 — Reset World State
> Before every test, the engine is re-initialized with a clean baseline.
> This simulates starting from a known, predictable database state.

```
ACC1 → $1,000.00
ACC2 → $500.00
```

---

### ✅ Step 2 — Test: Atomic Transfer Integrity

**Goal:** Confirm that funds move correctly and balances remain consistent.

```
transfer("ACC1" → "ACC2", $200)

Expected:
  ACC1 → $800.00  ✅
  ACC2 → $700.00  ✅
```

---

### ❌ Step 3 — Test: Insufficient Funds (Rollback Simulation)

**Goal:** Confirm that a failed withdrawal leaves state untouched.

```
withdraw("ACC1", $2,000)  ← exceeds balance

Expected:
  Exception thrown        ✅
  ACC1 still → $1,000.00  ✅
```

---

### 📋 Step 4 — Test: Audit Triggers Are Firing

**Goal:** Verify that every operation produces a log entry automatically.

```
Operations performed:
  1. deposit (setup)
  2. deposit (setup)
  3. deposit $100 → ACC1
  4. withdraw $50 ← ACC1

Expected:
  Audit log size = 4       ✅
  Last entry = WITHDRAWAL  ✅
```

---

## 🧠 Design Principles

- 🔁 **Inevitability** — Triggers fire on every operation without exception
- 🛡️ **Immutability on failure** — Failed operations never partially change state
- 🧹 **Clean baseline** — Each test starts from a fresh, defined world state
- 🔗 **Atomicity** — Transfer is a single logical unit; partial execution is not possible