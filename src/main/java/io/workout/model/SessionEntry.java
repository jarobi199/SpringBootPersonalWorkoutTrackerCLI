package io.workout.model;

import io.workout.enums.ExerciseType;

public record SessionEntry(String exerciseId, String exerciseName, ExerciseType exerciseType, int sets, int reps, int weightKg, int duration, int distanceKm, int addedWeightKg, String notes) {
}

