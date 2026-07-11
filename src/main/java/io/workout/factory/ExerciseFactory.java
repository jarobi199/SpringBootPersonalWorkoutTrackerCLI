package io.workout.factory;

import io.workout.enums.ExerciseType;
import io.workout.model.BodyweightExercise;
import io.workout.model.CardioExercise;
import io.workout.model.Exercise;
import io.workout.model.StrengthExercise;

public class ExerciseFactory {

    public static Exercise createExercise(ExerciseType exerciseType) {
        return switch (exerciseType) {
            case CARDIO -> new CardioExercise();
            case BODYWEIGHT -> new BodyweightExercise();
            case STRENGTH ->  new StrengthExercise();
        };
    }

}
