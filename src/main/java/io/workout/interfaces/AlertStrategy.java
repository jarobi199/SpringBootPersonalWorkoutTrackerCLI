package io.workout.interfaces;

import io.workout.alert.AlertContext;

public interface AlertStrategy {
    boolean supports(AlertContext context);
    String evaluate(AlertContext context);
}