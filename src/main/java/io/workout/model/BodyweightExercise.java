package io.workout.model;

import io.workout.authentication.SessionContext;
import io.workout.enums.Equipment;
import io.workout.enums.ExerciseType;
import io.workout.enums.MuscleGroup;

public class BodyweightExercise extends Exercise {

    public BodyweightExercise() {
        //No argument exercise
    }

    public BodyweightExercise(String userId, String name, MuscleGroup muscleGroup, Equipment equipment, String notes) {
        super(userId, name, muscleGroup, equipment, notes);
    }

    @Override
    public int calculateVolume(SessionEntry sessionEntry) {
        return sessionEntry.reps() * sessionEntry.sets() * (SessionContext.getUser().getBodyWeightKg() + sessionEntry.addedWeightKg());
    }

    @Override
    public ExerciseType getExerciseType() {
        return ExerciseType.BODYWEIGHT;
    }

}
