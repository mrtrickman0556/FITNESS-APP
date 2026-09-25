package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessDao {

    // --- User Profile ---
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    // --- Exercises ---
    @Query("SELECT * FROM exercises")
    fun getAllExercises(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE id = :id LIMIT 1")
    suspend fun getExerciseById(id: String): Exercise?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>)

    // --- Workout Plans ---
    @Query("SELECT * FROM workout_plans WHERE dayOfWeek = :day AND goal = :goal ORDER BY orderIndex ASC")
    fun getPlanForDay(day: Int, goal: String): Flow<List<WorkoutPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutPlans(plans: List<WorkoutPlan>)

    // --- Workout Logs ---
    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllWorkoutLogs(): Flow<List<WorkoutLog>>

    @Query("SELECT * FROM workout_logs WHERE exerciseId = :exerciseId ORDER BY timestamp DESC")
    fun getLogsForExercise(exerciseId: String): Flow<List<WorkoutLog>>

    @Query("SELECT MAX(weight) FROM workout_logs WHERE exerciseId = :exerciseId")
    fun getMaxWeightForExercise(exerciseId: String): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(log: WorkoutLog)

    @Query("DELETE FROM workout_logs WHERE id = :id")
    suspend fun deleteWorkoutLogById(id: Int)

    // --- Body Stats ---
    @Query("SELECT * FROM body_stat_logs ORDER BY timestamp DESC")
    fun getAllBodyStats(): Flow<List<BodyStatLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyStat(stat: BodyStatLog)

    // --- Meal Logs ---
    @Query("SELECT * FROM meal_logs ORDER BY timestamp DESC")
    fun getAllMealLogs(): Flow<List<MealLog>>

    @Query("SELECT * FROM meal_logs WHERE timestamp >= :startOfDay ORDER BY timestamp DESC")
    fun getTodayMealLogs(startOfDay: Long): Flow<List<MealLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealLog(meal: MealLog)

    @Query("DELETE FROM meal_logs WHERE id = :id")
    suspend fun deleteMealLogById(id: Int)

    // --- Recovery Logs ---
    @Query("SELECT * FROM recovery_logs ORDER BY timestamp DESC")
    fun getAllRecoveryLogs(): Flow<List<RecoveryLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecoveryLog(log: RecoveryLog)
}
