package com.protocol.trace;

import org.junit.jupiter.api.extension.*;

public class StateTraceExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        System.out.println("\n┌─────────────────────────────────────────────");
        System.out.println("│ TEST : " + context.getDisplayName());
        System.out.println("│ STATE: INITIALIZING");
        System.out.println("└─────────────────────────────────────────────");
    }

    @Override
    public void afterEach(ExtensionContext context) {
        boolean passed = context.getExecutionException().isEmpty();
        System.out.println("┌─────────────────────────────────────────────");
        System.out.println("│ TEST : " + context.getDisplayName());
        System.out.println("│ STATE: " + (passed ? "✔ OK" : "✘ FAILED"));
        context.getExecutionException().ifPresent(e ->
            System.out.println("│ ERROR: " + e.getMessage())
        );
        System.out.println("└─────────────────────────────────────────────\n");
    }
}