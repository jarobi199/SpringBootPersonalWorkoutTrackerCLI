package io.workout.alert;

import io.workout.interfaces.AlertStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlertManager {
    private final List<AlertStrategy> strategies;

    public AlertManager(List<AlertStrategy> strategies) {
        this.strategies = strategies;
    }

    public void evaluate(AlertContext context) {
        strategies.stream()
                .filter(s -> s.supports(context))
                .forEach(s -> s.evaluate(context));
    }
}