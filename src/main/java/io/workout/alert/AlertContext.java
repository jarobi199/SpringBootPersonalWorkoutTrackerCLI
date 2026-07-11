package io.workout.alert;

import io.workout.model.SessionEntry;
import io.workout.model.WorkoutSession;

public record AlertContext (WorkoutSession workoutSession, SessionEntry sessionEntry) {}

