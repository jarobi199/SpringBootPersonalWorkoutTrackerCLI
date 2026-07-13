package io.workout.service;

import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.components.Table;
import io.github.kusoroadeolu.clique.configuration.TableType;
import io.workout.authentication.SessionContext;
import io.workout.model.Exercise;
import io.workout.model.SessionEntry;
import io.workout.model.WorkoutSession;
import io.workout.repository.ExerciseRepository;
import io.workout.repository.WorkoutSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private WorkoutSessionRepository  workoutSessionRepository;

    public void topExercises() {
        Map<Exercise, List<SessionEntry>> exerciseListMap = new HashMap<>();
        List<Exercise> exercises = exerciseRepository.findByUserId(SessionContext.getUser().getId());
        for (Exercise exercise : exercises) {
            List<WorkoutSession> workoutSessions = workoutSessionRepository.findByUserIdOrderBySessionDateTimeDesc(SessionContext.getUser().getId());
            for (WorkoutSession workoutSession : workoutSessions) {
                List<SessionEntry> sessionEntries = workoutSession.getSessionEntries().stream()
                        .filter(sessionEntry ->  sessionEntry.exerciseId().equals(exercise.getId())).collect(Collectors.toCollection(ArrayList::new));
                if(!sessionEntries.isEmpty()) {
                    if(exerciseListMap.containsKey(exercise))
                    {
                        exerciseListMap.get(exercise).addAll(sessionEntries);
                    }
                    else
                    {
                        exerciseListMap.put(exercise, sessionEntries);
                    }
                }
            }
        }

        if(!exerciseListMap.isEmpty()){
            System.out.println();
            System.out.println("| TOP EXERCISES |");
            Table topExercisesTable = Clique.table(TableType.BOX_DRAW)
                    .headers(
                            "[*blue, bold]EXERCISE NAME[/]",
                            "[*blue, bold]EXERCISE TYPE[/]",
                            "[*blue, bold]TOTAL ENTRIES[/]",
                            "[*blue, bold]TOTAL VOLUME[/]",
                            "[*blue, bold]AVERAGE VOLUME PER ENTRY[/]"
                    );

            Map<Exercise, List<SessionEntry>> exerciseListMapSorted = exerciseListMap.entrySet()
                    .stream()
                    .sorted(Comparator.comparingInt(entry -> entry.getValue().size()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue,
                            LinkedHashMap::new
                    )).reversed();

            for (Map.Entry<Exercise, List<SessionEntry>> entry : exerciseListMapSorted.entrySet()) {
                int totalEntries = entry.getValue().size();
                int totalVolume = entry.getValue().stream().mapToInt(e -> entry.getKey().calculateVolume(e)).sum();
                double averageVolume =  entry.getValue().stream().mapToInt(e -> entry.getKey().calculateVolume(e)).average().getAsDouble();
                topExercisesTable.row(entry.getKey().getName(), entry.getKey().getExerciseType().name(), String.valueOf(totalEntries), String.valueOf(totalVolume), String.valueOf(averageVolume));
            }
            topExercisesTable.render();
        }
    }
}
