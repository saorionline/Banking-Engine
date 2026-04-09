package com.protocol.audit;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuditLogger {

    private final List<String> log = new ArrayList<>();

    public void log(String action, String accountId, String amount) {
        String entry = String.format("[%s] AUDIT: %s | Account: %s | Amount: %s",
                Instant.now(), action, accountId, amount);
        log.add(entry);
    }

    public List<String> getLog() {
        return Collections.unmodifiableList(log);
    }

    public void clear() {
        log.clear();
    }
}