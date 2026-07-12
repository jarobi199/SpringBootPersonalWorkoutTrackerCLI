package io.workout.service;

import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.components.Table;
import io.github.kusoroadeolu.clique.configuration.TableType;
import io.workout.authentication.SessionContext;
import io.workout.enums.Equipment;
import io.workout.enums.ExerciseType;
import io.workout.enums.MuscleGroup;
import io.workout.factory.ExerciseFactory;
import io.workout.model.Exercise;
import io.workout.model.SessionEntry;
import io.workout.model.WorkoutSession;
import io.workout.repository.ExerciseRepository;
import io.workout.repository.WorkoutSessionRepository;
import io.workout.util.SparklineUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExerciseService {
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;

    public void listExercises() {
        List<Exercise> exercises = exerciseRepository.findByUserId(SessionContext.getUser().getId());
        if(exercises.isEmpty()) {
            System.out.println("No exercises found.");
        }
        else {
            Table exerciseTable = Clique.table(TableType.BOX_DRAW)
                    .headers(
                            "[*blue, bold]NAME[/]",
                            "[*blue, bold]TYPE[/]",
                            "[*blue, bold]MUSCLE GROUP[/]",
                            "[*blue, bold]EQUIPMENT[/]"
                    );

            for (Exercise exercise : exercises) {
                exerciseTable.row(exercise.getName(), exercise.getExerciseType().name(), exercise.getMuscleGroup().name(), exercise.getEquipment().name());
            }
            exerciseTable.render();
        }
    }

    public void createExercise(ExerciseType exerciseType, String exerciseName, MuscleGroup muscleGroup, Equipment equipment, String notes) {
        Exercise exercise = ExerciseFactory.createExercise(exerciseType);
        exercise.setUserId(SessionContext.getUser().getId());
        exercise.setName(exerciseName);
        exercise.setMuscleGroup(muscleGroup);
        exercise.setEquipment(equipment);
        exercise.setNotes(notes);

        exerciseRepository.save(exercise);
    }

    public List<Exercise> findAllExercises() {
       return exerciseRepository.findByUserId(SessionContext.getUser().getId());
    }

    public boolean deleteExercise(Exercise exercise) {
        boolean success = false;
        List<WorkoutSession> workoutSessions = workoutSessionRepository.findByUserIdOrderBySessionDateTimeDesc(SessionContext.getUser().getId())
                .stream().filter(workoutSession -> workoutSession.getSessionEntries().stream().anyMatch(sessionEntry -> sessionEntry.exerciseId().equals(exercise.getId()))).toList();
        if(workoutSessions.isEmpty()) {
            exerciseRepository.delete(exercise);
            success = true;
        }

        return success;
    }

    public void viewDetails(Exercise exercise) {
        System.out.println("| EXERCISE |");
        Table exerciseTable = Clique.table(TableType.BOX_DRAW)
                .headers(
                        "[*blue, bold]NAME[/]",
                        "[*blue, bold]TYPE[/]",
                        "[*blue, bold]MUSCLE GROUP[/]",
                        "[*blue, bold]EQUIPMENT[/]",
                        "[*blue, bold]NOTES[/]"
                )
        .row(exercise.getName(), exercise.getExerciseType().name(), exercise.getMuscleGroup().name(), exercise.getEquipment().name(),  exercise.getNotes());
        exerciseTable.render();

        List<WorkoutSession> workoutSessions = workoutSessionRepository.findByUserIdOrderBySessionDateTimeDesc(SessionContext.getUser().getId());
        List<SessionEntry> sessionEntries = new ArrayList<>();
        workoutSessions.forEach(workoutSession -> sessionEntries.addAll(workoutSession.getSessionEntries().stream().filter(sessionEntry -> sessionEntry.exerciseId().equals(exercise.getId())).toList()));

        if (!sessionEntries.isEmpty()) {
            List<Integer> weightsKg = new ArrayList<>();
            List<Integer> reps = new ArrayList<>();
            List<Double> paces = new ArrayList<>();
            System.out.println();
            System.out.println("| SESSION ENTRIES |");

            if (ExerciseType.STRENGTH.equals(exercise.getExerciseType())) {
                Table sessionEntryTable = Clique.table(TableType.BOX_DRAW)
                        .headers(
                                "[*blue, bold]EXERCISE NAME[/]",
                                "[*blue, bold]EXERCISE TYPE[/]",
                                "[*blue, bold]WEIGHT (KG)[/]",
                                "[*blue, bold]DURATION[/]",
                                "[*blue, bold]NOTES[/]"
                        );
                for(SessionEntry sessionEntry : sessionEntries) {
                    sessionEntryTable.row(sessionEntry.exerciseName(), sessionEntry.exerciseType().name(), String.valueOf(sessionEntry.weightKg()), String.valueOf(sessionEntry.duration()), sessionEntry.notes());
                    weightsKg.add(sessionEntry.weightKg());
                }
                sessionEntryTable.render();

                System.out.println();
                System.out.println("| PROGRESSION GRAPH |");
                System.out.println(SparklineUtil.renderLabeled("Weight", weightsKg, "Kg"));
            }
            else if (ExerciseType.BODYWEIGHT.equals(exercise.getExerciseType())) {
                Table sessionEntryTable = Clique.table(TableType.BOX_DRAW)
                        .headers(
                                "[*blue, bold]EXERCISE NAME[/]",
                                "[*blue, bold]EXERCISE TYPE[/]",
                                "[*blue, bold]REPS[/]",
                                "[*blue, bold]DURATION[/]",
                                "[*blue, bold]NOTES[/]"
                        );
                for(SessionEntry sessionEntry : sessionEntries) {
                    sessionEntryTable.row(sessionEntry.exerciseName(), sessionEntry.exerciseType().name(), String.valueOf(sessionEntry.reps()), String.valueOf(sessionEntry.duration()), sessionEntry.notes());
                    reps.add(sessionEntry.reps());
                }
                sessionEntryTable.render();

                System.out.println();
                System.out.println("| PROGRESSION GRAPH |");
                System.out.println(SparklineUtil.renderLabeled("Reps", reps, ""));
            }
            else if (ExerciseType.CARDIO.equals(exercise.getExerciseType())) {
                Table sessionEntryTable = Clique.table(TableType.BOX_DRAW)
                        .headers(
                                "[*blue, bold]EXERCISE NAME[/]",
                                "[*blue, bold]EXERCISE TYPE[/]",
                                "[*blue, bold]PACE[/]",
                                "[*blue, bold]DURATION[/]",
                                "[*blue, bold]NOTES[/]"
                        );
                for(SessionEntry sessionEntry : sessionEntries) {
                    double pace = (double) sessionEntry.duration() / sessionEntry.distanceKm();
                    sessionEntryTable.row(sessionEntry.exerciseName(), sessionEntry.exerciseType().name(), String.valueOf(pace), String.valueOf(sessionEntry.duration()), sessionEntry.notes());
                    paces.add(pace);
                }
                sessionEntryTable.render();

                System.out.println();
                System.out.println("| PROGRESSION GRAPH |");
                System.out.println(SparklineUtil.renderLabeledDoubles("Paces", paces, ""));
            }
        }

    }
}
