package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val goal: String = "Muscle Gain", // "Muscle Gain", "Strength", "Fat Loss"
    val weightKg: Double = 80.0,
    val targetCalories: Int = 2800,
    val targetProtein: Int = 160,
    val experienceLevel: String = "Intermediate" // "Beginner", "Intermediate", "Advanced"
)

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey val id: String,
    val name: String,
    val muscleGroup: String, // Chest, Back, Legs, Shoulders, Arms, Core
    val equipment: String, // Barbell, Dumbbell, Machine, Bodyweight
    val videoSimulationType: String,
    val instructions: String,
    val defaultSets: Int = 4,
    val defaultReps: Int = 10,
    val defaultRestSeconds: Int = 90
)

@Entity(tableName = "workout_plans")
data class WorkoutPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayOfWeek: Int, // 1 = Monday, 2 = Tuesday, etc.
    val goal: String, // "Muscle Gain", "Strength", "Fat Loss"
    val exerciseId: String,
    val sets: Int,
    val reps: Int,
    val orderIndex: Int
)

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exerciseId: String,
    val exerciseName: String,
    val weight: Double,
    val reps: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val isPR: Boolean = false
)

@Entity(tableName = "body_stat_logs")
data class BodyStatLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val weight: Double,
    val bodyFat: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "meal_logs")
data class MealLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mealName: String,
    val calories: Int,
    val protein: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "recovery_logs")
data class RecoveryLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sleepHours: Double,
    val stretchingCompleted: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
