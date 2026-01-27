/* (The PROCEDURES and TRIGGERS). */

CREATE OR REPLACE PROCEDURE deposit_money (
    p_account_id IN NUMBER,
    p_amount IN NUMBER
) AS
BEGIN
    -- 1. Update the balance
    UPDATE accounts 
    SET balance = balance + p_amount 
    WHERE account_id = p_account_id;

    -- 2. Manually log the action
    INSERT INTO transaction_log (account_id, action_type, amount)
    VALUES (p_account_id, 'DEPOSIT', p_amount);
    
    COMMIT; -- Save changes permanently
    DBMS_OUTPUT.PUT_LINE('Deposit Successful for Account ' || p_account_id);
END;
/

-- 2. Trigger for Automatic Auditing
CREATE OR REPLACE TRIGGER audit_balance_change
AFTER UPDATE ON accounts
FOR EACH ROW
BEGIN
    INSERT INTO transaction_log (account_id, action_type, amount)
    VALUES (:NEW.account_id, 'BALANCE_UPDATE', :NEW.balance - :OLD.balance);
END;
/