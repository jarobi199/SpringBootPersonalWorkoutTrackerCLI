package io.workout.service;

import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.components.Table;
import io.github.kusoroadeolu.clique.configuration.TableType;
import io.workout.alert.AlertContext;
import io.workout.alert.AlertManager;
import io.workout.authentication.SessionContext;
import io.workout.enums.ExerciseType;
import io.workout.enums.MuscleGroup;
import io.workout.model.Exercise;
import io.workout.model.ExerciseProgression;
import io.workout.model.SessionEntry;
import io.workout.model.WorkoutSession;
import io.workout.repository.ExerciseRepository;
import io.workout.repository.WorkoutSessionRepository;
import io.workout.util.BarChartUtil;
import io.workout.util.InputHandler;
import io.workout.util.SparklineUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private WorkoutSessionRepository  workoutSessionRepository;
    @Autowired
    private AlertManager alertManager;

    public void topExercises() {
        Map<Exercise, List<SessionEntry>> exerciseListMap = new HashMap<>();
        List<Exercise> exercises = exerciseRepository.findByUserId(SessionContext.getUser().getId());
        List<WorkoutSession> workoutSessions = workoutSessionRepository.findByUserIdOrderBySessionDateTimeDesc(SessionContext.getUser().getId());

        for (Exercise exercise : exercises) {
            List<SessionEntry> sessionEntries = getAllSessionEntriesByExercise(exercise, workoutSessions);
                if(!sessionEntries.isEmpty()) {
                    exerciseListMap.put(exercise, sessionEntries);
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
                            (oldValue, _) -> oldValue,
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

    public void exerciseProgression(Exercise exercise, LocalDateTime startDate, LocalDateTime endDate) {
        List<ExerciseProgression> exerciseProgressions = new ArrayList<>();
        List<WorkoutSession> workoutSessions = workoutSessionRepository.findByUserIdAndSessionDateTimeBetween(SessionContext.getUser().getId(), startDate, endDate);
        for (WorkoutSession workoutSession : workoutSessions) {
            for(SessionEntry sessionEntry : workoutSession.getSessionEntries()) {
                if(sessionEntry.exerciseId().equals(exercise.getId())) {
                    exerciseProgressions.add(new ExerciseProgression(workoutSession.getSessionDateTime(), sessionEntry));
                }
            }
        }

        System.out.println("| EXERCISE PROGRESSION TABLE |");
        System.out.println("| EXERCISE: " +  exercise.getExerciseDisplay() + " |");
        Table sessionEntryTable = Clique.table(TableType.BOX_DRAW)
                .headers(
                        "[*blue, bold]DATE[/]",
                        "[*blue, bold]SETS[/]",
                        "[*blue, bold]REPS[/]",
                        "[*blue, bold]WEIGHT (KG)[/]",
                        "[*blue, bold]DURATION[/]",
                        "[*blue, bold]DISTANCE (KM)[/]",
                        "[*blue, bold]ADDED WEIGHT (KG)[/]",
                        "[*blue, bold]NOTES[/]"
                );
        for(ExerciseProgression exerciseProgression : exerciseProgressions) {
            sessionEntryTable.row(exerciseProgression.sessionDate().toString(), String.valueOf(exerciseProgression.sessionEntry().sets()), String.valueOf(exerciseProgression.sessionEntry().reps()), String.valueOf(exerciseProgression.sessionEntry().weightKg()),
                    String.valueOf(exerciseProgression.sessionEntry().duration()), String.valueOf(exerciseProgression.sessionEntry().distanceKm()), String.valueOf(exerciseProgression.sessionEntry().addedWeightKg()), exerciseProgression.sessionEntry().notes());
        }
        sessionEntryTable.render();
        System.out.println();

       System.out.println(" | SPARKLINE TABLE |");
        if(ExerciseType.CARDIO.equals(exercise.getExerciseType())) {
            List<Integer> values = exerciseProgressions.stream()
                    .map(exerciseProgression -> (exerciseProgression.sessionEntry().duration() / exerciseProgression.sessionEntry().distanceKm()))
                    .collect(Collectors.toList());
            System.out.println(SparklineUtil.renderLabeled("Pace", values, ""));
        }
        if(ExerciseType.BODYWEIGHT.equals(exercise.getExerciseType())) {
            List<Integer> values = exerciseProgressions.stream()
                    .map(exerciseProgression -> exerciseProgression.sessionEntry().reps()).toList();
            System.out.println(SparklineUtil.renderLabeled("Reps", values, ""));
        }
        if(ExerciseType.STRENGTH.equals(exercise.getExerciseType())) {
            List<Integer> values = exerciseProgressions.stream()
                    .map(exerciseProgression -> exerciseProgression.sessionEntry().weightKg()).toList();
            System.out.println(SparklineUtil.renderLabeled("Weight", values, "Kg"));
        }
    }

    private List<SessionEntry> getAllSessionEntriesByExercise(Exercise exercise, List<WorkoutSession> workoutSessions) {
        List<SessionEntry> sessionEntries = new ArrayList<>();
        for (WorkoutSession workoutSession : workoutSessions) {
            List<SessionEntry> entries = workoutSession.getSessionEntries()
                    .stream()
                    .filter(sessionEntry -> sessionEntry.exerciseId().equals(exercise.getId()))
                    .collect(Collectors.toCollection(ArrayList::new));

            if(!entries.isEmpty()) {
                sessionEntries.addAll(entries);
            }
        }

        return sessionEntries;
    }

    private int getTotalVolume(List<WorkoutSession> workoutSessions) {
        int totalVolume = 0;

        for(WorkoutSession workoutSession : workoutSessions) {
            for(SessionEntry sessionEntry : workoutSession.getSessionEntries()) {
                Exercise exercise = exerciseRepository.findById(sessionEntry.exerciseId()).orElse(null);
                if(exercise != null) {
                    totalVolume = totalVolume + exercise.calculateVolume(sessionEntry);
                }
            }
        }

        return totalVolume;
    }

    public void volumeOverTime() {
        int weeklyTotalVolume = 0;
        List<WorkoutSession> workoutSessions = null;
        BarChartUtil.Builder chartBuilder = BarChartUtil.builder().title("TOTAL WEEKLY TRAINING VOLUME");
        LocalDateTime startDateTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endDateTime = LocalDateTime.now();

        for(int i =1; i <= 8; i++) {
            workoutSessions = workoutSessionRepository.findByUserIdAndSessionDateTimeBetween(SessionContext.getUser().getId(), startDateTime, endDateTime);
            weeklyTotalVolume = getTotalVolume(workoutSessions);
            chartBuilder.bar("Week " + i, weeklyTotalVolume);

            endDateTime = startDateTime;
            startDateTime = startDateTime.minusDays(7);
        }

        chartBuilder.render();
    }

    public void weeklySummary() {
        Map<WorkoutSession, Integer> workoutSessionMap = new HashMap<>();
        Map<MuscleGroup, Integer> muscleGroupMap = new EnumMap<>(MuscleGroup.class);
        List<WorkoutSession> workoutSessions = workoutSessionRepository.findByUserIdAndSessionDateTimeBetween(SessionContext.getUser().getId(), LocalDateTime.now().minusDays(7), LocalDateTime.now());
        int numberOfSessions = workoutSessions.size();
        int goal = SessionContext.getUser().getWeeklySessionGoal();

        for(WorkoutSession workoutSession : workoutSessions) {
            for(SessionEntry sessionEntry : workoutSession.getSessionEntries()) {
                Exercise exercise = exerciseRepository.findById(sessionEntry.exerciseId()).orElse(null);

                if(exercise != null) {
                    //Populate workoutSessionMap to determine highest total volume single workout
                    if(workoutSessionMap.containsKey(workoutSession)) {
                        workoutSessionMap.put(workoutSession, workoutSessionMap.get(workoutSession) + exercise.calculateVolume(sessionEntry));
                    }
                    else
                    {
                        workoutSessionMap.put(workoutSession, exercise.calculateVolume(sessionEntry));
                    }
                    //Populate muscle group map to determine most trained muscle group
                    if(muscleGroupMap.containsKey(exercise.getMuscleGroup())) {
                        muscleGroupMap.replace(exercise.getMuscleGroup(), muscleGroupMap.get(exercise.getMuscleGroup()) + 1);
                    }
                    else
                    {
                        muscleGroupMap.put(exercise.getMuscleGroup(), 1);
                    }
                }
            }
        }

        MuscleGroup topMuscleGroup = muscleGroupMap.entrySet().
                stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, _) -> oldValue,
                        LinkedHashMap::new
                )).firstEntry().getKey();

        int totalVolume = workoutSessionMap.values().stream().mapToInt(Integer::intValue).sum();
        WorkoutSession longestWorkoutSession = workoutSessionMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, _) -> oldValue,
                        LinkedHashMap::new
                )).firstEntry().getKey();


        System.out.println("| WEEKLY SUMMARY |");
        Table weeklySummaryTable = Clique.table(TableType.BOX_DRAW)
                .headers(
                        "[*blue, bold]SESSIONS COMPLETED[/]",
                        "[*blue, bold]WEEKLY GOAL[/]",
                        "[*blue, bold]TOTAL VOLUME[/]",
                        "[*blue, bold]TOP MUSCLE GROUP[/]",
                        "[*blue, bold]LONGEST SESSION[/]"
                )
                .row(String.valueOf(numberOfSessions), String.valueOf(goal), String.valueOf(totalVolume), topMuscleGroup.name(), longestWorkoutSession.getDisplayName());
        weeklySummaryTable.render();

        alertManager.evaluate(new AlertContext(workoutSessions.getLast(), null));
    }

    public void monthlySummary() {
        System.out.println("Please enter a month as an integer (ex. January is 1, etc.):");
        int month = InputHandler.getIntegerInput();
        int year = Year.now().getValue();
        List<WorkoutSession> workoutSessions = workoutSessionRepository.findByUserIdAndSessionDateTimeBetween(SessionContext.getUser().getId(), LocalDateTime.of(year,month, 1,0, 0), LocalDateTime.of(year,month, 31,23, 59));
        int totalVolume = getTotalVolume(workoutSessions);
        int totalNumberOfSessions = workoutSessions.size();

        System.out.println("| MONTHLY SUMMARY |");
        Table monthlySummaryTable = Clique.table(TableType.BOX_DRAW)
                .headers(
                        "[*blue, bold]TOTAL SESSIONS[/]",
                        "[*blue, bold]TOTAL VOLUME[/]",
                        "[*blue, bold]AVERAGE SESSION VOLUME[/]"
                )
                .row(String.valueOf(totalNumberOfSessions), String.valueOf(totalVolume), String.valueOf(totalVolume / totalNumberOfSessions));
        monthlySummaryTable.render();

        System.out.println("| VOLUME BY EXERCISE TYPE |");
        Table volumeByExerciseTable = Clique.table(TableType.BOX_DRAW)
                .headers(
                        "[*blue, bold]EXERCISE[/]",
                        "[*blue, bold]VOLUME[/]"
                );

        Map<Exercise, Integer> exerciseVolumeMap = new HashMap<>();
        List<Exercise> exercises = exerciseRepository.findByUserId(SessionContext.getUser().getId());
        for (Exercise exercise : exercises) {
            List<SessionEntry> sessionEntries = getAllSessionEntriesByExercise(exercise, workoutSessions);
            if(!sessionEntries.isEmpty()) {
                exerciseVolumeMap.put(exercise, sessionEntries.stream()
                        .mapToInt(exercise::calculateVolume).sum());
            }
        }

        for(Map.Entry<Exercise, Integer> entry : exerciseVolumeMap.entrySet()) {
            volumeByExerciseTable.row(entry.getKey().getExerciseDisplay(), String.valueOf(entry.getValue()));
        }
        volumeByExerciseTable.render();
    }

}
