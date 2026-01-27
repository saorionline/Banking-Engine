/* (The CREATE TABLE and INDEX commands). */
-- Table to store account info
CREATE TABLE accounts (
    account_id NUMBER PRIMARY KEY,
    owner_name VARCHAR2(100),
    balance NUMBER(15, 2) CHECK (balance >= 0) -- Constraint: No negative money!
);

-- Table to store transaction history (Audit Log)
CREATE TABLE transaction_log (
    log_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    account_id NUMBER,
    action_type VARCHAR2(20), -- 'DEPOSIT', 'WITHDRAW', 'TRANSFER'
    amount NUMBER(15, 2),
    t_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);