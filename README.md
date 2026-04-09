# 🏦 Banking Engine Simulation + 🧪 State Trace Extension

> A state-driven banking engine with **database-like behavior** + **beautiful, traceable JUnit 5 test output**.

This project combines two powerful ideas:

* 🏦 A **simulated banking engine** using repository + trigger patterns
* 🧪 A **JUnit 5 tracing system** that turns tests into readable execution stories

---

# 🧭 Overview

## 🏦 Banking Engine

A simulated **database-level engine** that models core banking operations:

* Procedures → business logic (deposit, withdraw, transfer)
* Triggers → automatic audit logging
* Constraints → validation + rollback simulation

---

## 🧪 State Trace System

A lightweight testing enhancement that provides:

* Structured test lifecycle logging
* Step-by-step state transitions
* Clear expected vs actual comparisons

---

# 🗂️ Architecture Overview

```
BankingEngine (Mock Database)
├── 🗃️  State        → Account Balances (in-memory store)
├── ⚙️  Procedures   → deposit / withdraw / transfer
└── 🔔  Triggers     → Automated audit logging
```

```
JUnit Test
   │
   ├── 🔌 StateTraceExtension (auto lifecycle logging)
   │
   └── 🛠️ StateTracer (manual trace utilities)
```

---

# ⚙️ Core Banking Procedures

| Procedure  | Description                      | Validation                 |
| ---------- | -------------------------------- | -------------------------- |
| `deposit`  | Adds funds to an account         | Amount must be > 0         |
| `withdraw` | Deducts funds                    | Balance must be sufficient |
| `transfer` | Atomic movement between accounts | Withdraw + Deposit         |

---

# 🔔 Trigger Behavior (Audit System)

Every operation automatically fires an internal **Audit Trigger**:

* ✅ No state change without a log entry
* ✅ Invisible to caller (fully automatic)
* ✅ Mimics real database triggers

---

# 🔒 Constraint Logic (Rollback Simulation)

Invalid operations never mutate state.

```
withdraw("ACC1", 9999)
  └── ❌ Insufficient funds
        └── State unchanged ✅
```

---

# 🧪 State Trace Extension (JUnit 5)

## 🔍 What It Does

Enhances test output with **structured execution tracing**.

### 🔌 `StateTraceExtension`

* Hooks into test lifecycle via `@ExtendWith`
* Logs:

  * 🧪 Test start
  * ✅ Success / ❌ Failure
* Requires **zero changes** to tests

---

### 🛠️ `StateTracer`

Manual helper for logging:

* 🔄 State transitions
* 🎯 Expected vs actual
* ⚠️ Expected exceptions

---

# 📦 Installation

```
src/test/java/your/package/
├── StateTraceExtension.java
└── StateTracer.java
```

---

# 🚀 Usage

## 1. Enable Extension

```java
@ExtendWith(StateTraceExtension.class)
class AccountServiceTest {
}
```

---

## 2. Log State Transitions

```java
StateTracer.logTransition(
    "ACC-001",
    100.00,
    "WITHDRAWAL $100.00",
    0.00
);
```

---

## 3. Validate Expectations

```java
StateTracer.logExpected(0.00, account.getBalance());
```

---

## 4. Log Expected Exceptions

```java
StateTracer.logException("Insufficient funds", () -> {
    account.withdraw(200.00);
});
```

---

# 🖥️ Example Output

```
┌─────────────────────────────────────────────
│ TEST : withdraw_exactBalance_shouldLeaveZero
│ STATE: INITIALIZING
└─────────────────────────────────────────────
│  ├─ Account   : ACC-001
│  ├─ Before    : $100.00
│  ├─ Operation : WITHDRAWAL $100.00
│  └─ After     : $0.00
│  ├─ Expected  : $0.00
│  ├─ Actual    : $0.00
│  └─ Match     : ✔ OK
┌─────────────────────────────────────────────
│ TEST : withdraw_exactBalance_shouldLeaveZero
│ STATE: ✔ OK
└─────────────────────────────────────────────
```

---

# 🧪 Unit Test Scenarios

## 🔧 Step 1 — Reset World State

```
ACC1 → $1,000.00
ACC2 → $500.00
```

---

## ✅ Step 2 — Atomic Transfer

```
transfer("ACC1" → "ACC2", $200)

Expected:
  ACC1 → $800.00
  ACC2 → $700.00
```

---

## ❌ Step 3 — Rollback Simulation

```
withdraw("ACC1", $2,000)

Expected:
  Exception thrown
  State unchanged
```

---

## 📋 Step 4 — Audit Trigger Verification

```
Operations:
  deposit
  deposit
  deposit $100
  withdraw $50

Expected:
  Audit log size = 4
  Last entry = WITHDRAWAL
```

---

# 🧠 Design Principles

* 🔁 **Inevitability** → Triggers always fire
* 🛡️ **Immutability on failure** → No partial updates
* 🧹 **Clean baseline** → Tests start fresh
* 🔗 **Atomicity** → Transfers are indivisible
* 🔍 **Observability** → Every state change is visible

---

# ✨ When to Use This

Perfect for:

* 💳 Financial systems
* 🛒 Transactional workflows
* 🔄 Stateful services
* 🧩 Complex domain logic
* 🧪 Debug-heavy test suites

---

# 💡 Pro Tips

* Log every meaningful state change
* Pair assertions with `logExpected()`
* Use clear operation names (`TRANSFER $200`)
* Keep IDs consistent for traceability

---

# 🛠️ Future Improvements

* 📊 JSON / structured logs
* 🎨 ANSI color output
* 🌐 Test reporting integration
* ⏱️ Step-level timing

---

# 🤝 Contributing

Contributions are welcome! Improve tracing, formatting, or extend features.

---

# 📄 License

MIT License

---

# ❤️ Philosophy

> Your tests shouldn't just pass — they should **tell a story**.

This project turns:

* ❌ Raw logs
  into
* ✅ Clear, structured execution narratives

---

