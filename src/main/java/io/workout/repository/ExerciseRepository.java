package io.workout.repository;

import io.workout.enums.Equipment;
import io.workout.enums.MuscleGroup;
import io.workout.model.Exercise;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends MongoRepository<Exercise, String> {
    List<Exercise> findByUserId(String userId);
    List<Exercise> findByUserIdAndMuscleGroup(String userId, MuscleGroup muscleGroup);
    List<Exercise> findByUserIdAndEquipment(String userId, Equipment equipment);
}
