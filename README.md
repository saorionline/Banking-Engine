# Banking Engine — Database‑First Design

## Overview

This project represents a **Banking Engine mockup** where the main logic lives **inside the database**, not inside the application.

In real banking systems, money operations should **never depend on an app staying open or not crashing**. For that reason, this project focuses less on Java logic and more on **Database logic**, using **Oracle SQL and PL/SQL** to safely handle balances and transactions.

The goal is simple:

> **If something goes wrong in the app, the money must still be safe.**

---

## Why the Database Is the "Brain"

In many beginner projects, calculations (like deposits or withdrawals) are done in the application code. That works for demos, but it is **dangerous for real banking systems**.

In this project:

* The **database** performs the calculations
* The **database** saves the transaction history
* The **database** guarantees that money never disappears

The application (for example, a Java app) only **asks** the database to do things — it does not decide the rules.

---

## The Basic Structure (The Mockup)

To understand how a banking engine works internally, this mockup uses two main tables:

### 1. Accounts Table (Current Balance)

This table stores who owns the account and how much money they currently have.

```sql
CREATE OR REPLACE PROCEDURE deposit_money (
p_account_id IN NUMBER,
p_amount IN NUMBER
) AS
BEGIN
UPDATE accounts
SET balance = balance + p_amount
WHERE account_id = p_account_id;


INSERT INTO transaction_log (account_id, action_type, amount)
VALUES (p_account_id, 'DEPOSIT', p_amount);
COMMIT;
END;
/
```

**What this means in simple words:**

* Each account has an ID and an owner. Adds money to the account
* The balance **can never be negative**
* The database itself enforces this rule, not the app
* Records the action in the history
* Saves everything safely
The application never touches the balance directly — it only asks the database to perform a trusted action.
---

### 2. Transaction Log (History / Audit Log)

This table keeps a record of **every movement of money**.

```sql
CREATE OR REPLACE TRIGGER audit_balance_change
AFTER UPDATE ON accounts
FOR EACH ROW
BEGIN
INSERT INTO transaction_log (account_id, action_type, amount)
VALUES (:NEW.account_id, 'BALANCE_UPDATE', :NEW.balance - :OLD.balance);
END;
/
```

**Why this table matters:**

* Banks must always know **what happened, when, and how much**
* Even if a mistake happens, the history is never lost
* This is how audits and investigations are done in real systems
* Runs automatically
* Cannot be skipped or forgotten
* Logs every balance change, no matter where it comes from
* Think of this as a security camera watching every movement.
---

## The Test Scenario (Proving It Works)

This file is used to populate the database and test the engine.

What it does

Creates sample accounts

Runs a real deposit using the procedure

Shows the final results:

```sql
INSERT INTO accounts (account_id, owner_name, balance) VALUES (1, 'Sao', 1000.00);
INSERT INTO accounts (account_id, owner_name, balance) VALUES (2, 'John Doe', 50.00);


BEGIN
deposit_money(1, 500.00);
END;
/


SELECT * FROM accounts;
SELECT * FROM transaction_log;
```

**In plain words:**

1. The database adds money to the account
2. The database records what happened
3. The database saves everything permanently

Even if the application crashes right after this, the money is already safe.

---

## Automatic Safety: Triggers

A **Trigger** is an automatic action that runs when something changes.

You can think of it as a **security camera** that never sleeps.

This trigger watches for balance changes and logs them automatically:

```sql
CREATE OR REPLACE TRIGGER audit_balance_change
AFTER UPDATE ON accounts
FOR EACH ROW
BEGIN
    INSERT INTO transaction_log (account_id, action_type, amount)
    VALUES (:NEW.account_id, 'BALANCE_UPDATE', :NEW.balance - :OLD.balance);
END;
/
```

**Why this is powerful:**

* No developer can forget to log a transaction
* The database protects itself
* Every change leaves a trail

---

## Testing the Mockup

To test this banking engine, you can run simple commands:

1. **Create an account**

```
INSERT INTO accounts VALUES (101, 'Sao', 500.00);
```

2. **Deposit money using the engine**

```
EXEC deposit_money(101, 150.00);
```

3. **Check what happened**

```
SELECT * FROM transaction_log;
```

You should see a clear history showing exactly what changed.

---

## Why This Is a Technical Highlight (Without the Jargon)

* **Money Safety:** The database itself prevents impossible states (like negative money)
* **Reliability:** The logic keeps working even if the app fails
* **Traceability:** Every action is recorded forever
* **Scalability:** This design works for one user or millions of users

This is how real banking systems think: **the database is the source of truth**.

---

## How This Connects to Java Later

A Java program does **not** calculate balances directly.

Instead, it simply says:

> "Database, please run `deposit_money` for this account."

This keeps the application simple and the data safe.

---

## Final Note

This project is not about flashy interfaces.
It is about **trust, safety, and correctness** — the core values behind any real financial system.

The mockup may be small, but the ideas behind it are the same ones used in real-world banking engines.
