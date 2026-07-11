
package io.workout.repository;

import io.workout.model.WorkoutSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutSessionRepository extends MongoRepository<WorkoutSession, String> {
    List<WorkoutSession> findByUserIdOrderBySessionDateDesc(String userId);
    List<WorkoutSession> findTop10ByUserId(String userId);
}

