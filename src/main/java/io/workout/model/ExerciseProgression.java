package io.workout.model;

import java.time.LocalDateTime;

public record ExerciseProgression(LocalDateTime sessionDate, SessionEntry sessionEntry) {}
