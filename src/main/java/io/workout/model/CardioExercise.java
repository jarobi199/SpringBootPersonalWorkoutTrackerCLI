package io.workout.model;

import io.workout.enums.Equipment;
import io.workout.enums.ExerciseType;
import io.workout.enums.MuscleGroup;

public class CardioExercise extends Exercise {

    public CardioExercise() {
        //No argument constructor
    }

    public CardioExercise(String name, MuscleGroup muscleGroup, Equipment equipment, String notes) {
        super(name, muscleGroup, equipment, notes);
    }

    @Override
    public int calculateVolume(SessionEntry sessionEntry) {
        return sessionEntry.distanceKm() * 1000;
    }

    @Override
    public ExerciseType getExerciseType() {
        return ExerciseType.CARDIO;
    }

}

