/* The INSERT commands to populate the mockup). */

-- Create some initial accounts
INSERT INTO accounts (account_id, owner_name, balance) VALUES (1, 'Sao', 1000.00);
INSERT INTO accounts (account_id, owner_name, balance) VALUES (2, 'John Doe', 50.00);

-- Run a test deposit using your engine
BEGIN
    deposit_money(1, 500.00);
END;
/

-- Final check to see if everything worked
SELECT * FROM accounts;
SELECT * FROM transaction_log;