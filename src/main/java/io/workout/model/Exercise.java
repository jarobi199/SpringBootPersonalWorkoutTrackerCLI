package io.workout.model;

import io.workout.enums.Equipment;
import io.workout.enums.ExerciseType;
import io.workout.enums.MuscleGroup;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "exercises")
public abstract class Exercise {
    @Id
    protected String id;
    protected String name;
    protected MuscleGroup muscleGroup;
    protected Equipment equipment;
    protected ExerciseType exerciseType;
    protected String notes;

    public Exercise() {
        //No argument constructor
    }

    public Exercise(String name, MuscleGroup muscleGroup, Equipment equipment, String notes) {
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.equipment = equipment;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MuscleGroup getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(MuscleGroup muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public abstract int calculateVolume(SessionEntry sessionEntry);

    public abstract ExerciseType getExerciseType();

}
