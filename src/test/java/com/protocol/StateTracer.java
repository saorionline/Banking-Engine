package com.protocol.trace;

import java.math.BigDecimal;

public class StateTracer {

    public static void logTransition(String accountId, BigDecimal before, String operation, BigDecimal operationAmount, BigDecimal after) {
        System.out.println("│  ├─ Account   : " + accountId);
        System.out.println("│  ├─ Before    : " + format(before));
        System.out.println("│  ├─ Operation : " + operation + " " + format(operationAmount));
        System.out.println("│  └─ After     : " + format(after));
    }

    public static void logExpected(BigDecimal expected, BigDecimal actual) {
        boolean match = actual.compareTo(expected) == 0;
        System.out.println("│  ├─ Expected  : " + format(expected));
        System.out.println("│  ├─ Actual    : " + format(actual));
        System.out.println("│  └─ Match     : " + (match ? "✔ OK" : "✘ MISMATCH"));
    }

    public static void logException(String expectedType, String operation) {
        System.out.println("│  ├─ Operation : " + operation);
        System.out.println("│  └─ Expects   : " + expectedType + " → ✔ OK if thrown");
    }

    private static String format(BigDecimal value) {
        return value == null ? "N/A" : "$" + value.toPlainString();
    }
}