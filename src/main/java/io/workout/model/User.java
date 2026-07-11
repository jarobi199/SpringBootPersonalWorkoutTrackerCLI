package io.workout.model;

import io.workout.enums.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String username;
    private String password;
    private Role role;
    private int bodyWeightKg;
    private int weeklySessionGoal;

    private static final int WEEKLY_SESSION_GOAL_DEFAULT = 3;

    public User(String name, String username, String password, Role role, int bodyWeightKg) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.bodyWeightKg = bodyWeightKg;
        this.weeklySessionGoal = WEEKLY_SESSION_GOAL_DEFAULT;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getBodyWeightKg() {
        return bodyWeightKg;
    }

    public void setBodyWeightKg(int bodyWeightKg) {
        this.bodyWeightKg = bodyWeightKg;
    }

    public int getWeeklySessionGoal() {
        return weeklySessionGoal;
    }

    public void setWeeklySessionGoal(int weeklySessionGoal) {
        this.weeklySessionGoal = weeklySessionGoal;
    }

}
