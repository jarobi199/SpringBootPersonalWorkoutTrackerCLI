package io.workout.model;

import io.workout.enums.Equipment;
import io.workout.enums.ExerciseType;
import io.workout.enums.MuscleGroup;

public class StrengthExercise extends Exercise {

    public StrengthExercise() {
        //No argument constructor
    }

    public StrengthExercise(String userId, String name, MuscleGroup muscleGroup, Equipment equipment, String notes) {
        super(userId, name, muscleGroup, equipment, notes);
    }

    @Override
    public int calculateVolume(SessionEntry sessionEntry) {
        return sessionEntry.sets() * sessionEntry.reps() * sessionEntry.weightKg();
    }

    @Override
    public ExerciseType getExerciseType() {
        return ExerciseType.STRENGTH;
    }
}
