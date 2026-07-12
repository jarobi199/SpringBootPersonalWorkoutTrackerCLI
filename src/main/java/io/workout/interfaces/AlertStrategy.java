package io.workout.interfaces;

import io.workout.alert.AlertContext;

public interface AlertStrategy {
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RESET = "\u001B[0m";

    boolean supports(AlertContext context);
    void evaluate(AlertContext context);
}