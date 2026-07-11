package io.workout.alert;

import io.workout.interfaces.AlertStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlertManager {
    private final List<AlertStrategy> strategies;

    public AlertManager(List<AlertStrategy> strategies) {
        this.strategies = strategies;
    }

     public String evaluate(AlertContext context) {
        return strategies.stream()
                .filter(s -> s.supports(context))
                .map(s -> s.evaluate(context)) .collect(Collectors.joining("\n"));
    }

}