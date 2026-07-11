package io.workout.model;


import io.workout.enums.Equipment;
import io.workout.enums.ExerciseType;
import io.workout.enums.MuscleGroup;

public class BodyweightExercise extends Exercise {

    public BodyweightExercise() {
        //No argument exercise
    }

    public BodyweightExercise(String name, MuscleGroup muscleGroup, Equipment equipment, String notes) {
        super(name, muscleGroup, equipment, notes);
    }

    @Override
    public int calculateVolume(SessionEntry sessionEntry) {
        return sessionEntry.reps() * sessionEntry.sets() * (sessionEntry.weightKg() + sessionEntry.addedWeightKg());
    }

    @Override
    public ExerciseType getExerciseType() {
        return ExerciseType.BODYWEIGHT;
    }

}
