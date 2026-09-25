package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class FitnessRepository(private val fitnessDao: FitnessDao) {

    val userProfile: Flow<UserProfile?> = fitnessDao.getUserProfile()
    val allExercises: Flow<List<Exercise>> = fitnessDao.getAllExercises()
    val allWorkoutLogs: Flow<List<WorkoutLog>> = fitnessDao.getAllWorkoutLogs()
    val allBodyStats: Flow<List<BodyStatLog>> = fitnessDao.getAllBodyStats()
    val allMealLogs: Flow<List<MealLog>> = fitnessDao.getAllMealLogs()
    val allRecoveryLogs: Flow<List<RecoveryLog>> = fitnessDao.getAllRecoveryLogs()

    suspend fun getExerciseById(id: String): Exercise? = fitnessDao.getExerciseById(id)

    fun getPlanForDay(day: Int, goal: String): Flow<List<WorkoutPlan>> =
        fitnessDao.getPlanForDay(day, goal)

    fun getLogsForExercise(exerciseId: String): Flow<List<WorkoutLog>> =
        fitnessDao.getLogsForExercise(exerciseId)

    fun getMaxWeightForExercise(exerciseId: String): Flow<Double?> =
        fitnessDao.getMaxWeightForExercise(exerciseId)

    fun getTodayMealLogs(startOfDay: Long): Flow<List<MealLog>> =
        fitnessDao.getTodayMealLogs(startOfDay)

    suspend fun saveUserProfile(profile: UserProfile) {
        fitnessDao.insertUserProfile(profile)
    }

    suspend fun logWorkout(log: WorkoutLog) {
        fitnessDao.insertWorkoutLog(log)
    }

    suspend fun deleteWorkoutLog(id: Int) {
        fitnessDao.deleteWorkoutLogById(id)
    }

    suspend fun logBodyStat(stat: BodyStatLog) {
        fitnessDao.insertBodyStat(stat)
    }

    suspend fun logMeal(meal: MealLog) {
        fitnessDao.insertMealLog(meal)
    }

    suspend fun deleteMealLog(id: Int) {
        fitnessDao.deleteMealLogById(id)
    }

    suspend fun logRecovery(log: RecoveryLog) {
        fitnessDao.insertRecoveryLog(log)
    }

    suspend fun prepopulateIfNeeded() {
        val exercisesExist = fitnessDao.getAllExercises().first().isNotEmpty()
        if (!exercisesExist) {
            // 1. Insert Default User Profile
            fitnessDao.insertUserProfile(UserProfile())

            // 2. Insert Default Exercises
            val defaultExercises = listOf(
                Exercise(
                    id = "bench_press",
                    name = "Barbell Bench Press",
                    muscleGroup = "Chest",
                    equipment = "Barbell",
                    videoSimulationType = "bench_press",
                    instructions = "Lie flat on a bench. Grip the barbell slightly wider than shoulder-width. Lower the bar slowly to your mid-chest, keeping elbows at a 45-degree angle. Push the bar explosive back up until arms are fully extended.",
                    defaultSets = 4,
                    defaultReps = 8,
                    defaultRestSeconds = 90
                ),
                Exercise(
                    id = "dumbbell_fly",
                    name = "Incline Dumbbell Fly",
                    muscleGroup = "Chest",
                    equipment = "Dumbbell",
                    videoSimulationType = "dumbbell_fly",
                    instructions = "Set incline bench to 30 degrees. Hold dumbbells above chest, palms facing each other. Lower weights in a wide arc with slight elbow bend until you feel a deep stretch in your chest, then squeeze chest to bring them back up.",
                    defaultSets = 3,
                    defaultReps = 12,
                    defaultRestSeconds = 75
                ),
                Exercise(
                    id = "barbell_row",
                    name = "Bent-Over Barbell Row",
                    muscleGroup = "Back",
                    equipment = "Barbell",
                    videoSimulationType = "barbell_row",
                    instructions = "Hinge forward at the hips, keeping back flat and knees slightly bent. Hold barbell with shoulder-width grip. Pull the bar to your lower ribcage, driving elbows toward the ceiling. Lower with control.",
                    defaultSets = 4,
                    defaultReps = 8,
                    defaultRestSeconds = 90
                ),
                Exercise(
                    id = "pull_ups",
                    name = "Wide-Grip Pull-Up",
                    muscleGroup = "Back",
                    equipment = "Bodyweight",
                    videoSimulationType = "pull_ups",
                    instructions = "Hang from a bar with palms facing away, wider than shoulder-width. Pull your chest up toward the bar by driving your elbows down. Keep your core tight and avoid swinging. Lower slowly.",
                    defaultSets = 4,
                    defaultReps = 10,
                    defaultRestSeconds = 90
                ),
                Exercise(
                    id = "barbell_squat",
                    name = "Barbell Back Squat",
                    muscleGroup = "Legs",
                    equipment = "Barbell",
                    videoSimulationType = "barbell_squat",
                    instructions = "Rest barbell across upper traps, feet shoulder-width apart. Break at hips and knees, sitting back into a squat. Keep chest high and knees in line with feet. Go below parallel, then drive back up through heels.",
                    defaultSets = 4,
                    defaultReps = 6,
                    defaultRestSeconds = 120
                ),
                Exercise(
                    id = "romanian_deadlift",
                    name = "Barbell Romanian Deadlift",
                    muscleGroup = "Legs",
                    equipment = "Barbell",
                    videoSimulationType = "romanian_deadlift",
                    instructions = "Stand tall with barbell. Hinge at hips, pushing them backward. Keep back flat and barbell close to thighs, sliding it down until you feel a deep hamstring stretch. Squeeze glutes and hinge back up.",
                    defaultSets = 3,
                    defaultReps = 10,
                    defaultRestSeconds = 90
                ),
                Exercise(
                    id = "overhead_press",
                    name = "Barbell Overhead Press",
                    muscleGroup = "Shoulders",
                    equipment = "Barbell",
                    videoSimulationType = "overhead_press",
                    instructions = "Stand with barbell at collarbone level. Squeeze glutes and core. Press the bar straight overhead, moving your head back slightly to clear the bar. Lock arms out at top, shrug shoulders, and lower.",
                    defaultSets = 4,
                    defaultReps = 8,
                    defaultRestSeconds = 90
                ),
                Exercise(
                    id = "lateral_raise",
                    name = "Dumbbell Lateral Raise",
                    muscleGroup = "Shoulders",
                    equipment = "Dumbbell",
                    videoSimulationType = "lateral_raise",
                    instructions = "Hold dumbbells at your sides, slight bend in elbows. Raise arms out to the sides until parallel to the floor, leading with the pinky fingers. Pause at the top, then slowly lower to sides.",
                    defaultSets = 3,
                    defaultReps = 15,
                    defaultRestSeconds = 60
                ),
                Exercise(
                    id = "bicep_curl",
                    name = "Dumbbell Bicep Curl",
                    muscleGroup = "Arms",
                    equipment = "Dumbbell",
                    videoSimulationType = "bicep_curl",
                    instructions = "Hold dumbbells at sides, palms facing forward. Keep elbows pinned to ribs. Curl weights up toward shoulders, squeezing biceps at the top. Lower with control to a full stretch.",
                    defaultSets = 3,
                    defaultReps = 12,
                    defaultRestSeconds = 60
                ),
                Exercise(
                    id = "tricep_pushdown",
                    name = "Cable Tricep Pushdown",
                    muscleGroup = "Arms",
                    equipment = "Machine",
                    videoSimulationType = "tricep_pushdown",
                    instructions = "Grip cable bar or rope attachment. Keep elbows pinned to sides. Press attachment straight down until arms are fully locked, squeezing triceps. Control the weight on the way up.",
                    defaultSets = 3,
                    defaultReps = 12,
                    defaultRestSeconds = 60
                ),
                Exercise(
                    id = "cable_crunch",
                    name = "Kneeling Cable Crunch",
                    muscleGroup = "Core",
                    equipment = "Machine",
                    videoSimulationType = "cable_crunch",
                    instructions = "Kneel facing the cable machine, holding rope attachment behind your head. Keeping hips locked, flex your spine and pull elbows down to thighs. Focus on squeezing abs, then slowly return.",
                    defaultSets = 3,
                    defaultReps = 15,
                    defaultRestSeconds = 60
                ),
                Exercise(
                    id = "hanging_leg_raise",
                    name = "Hanging Knee/Leg Raise",
                    muscleGroup = "Core",
                    equipment = "Bodyweight",
                    videoSimulationType = "hanging_leg_raise",
                    instructions = "Hang from pull-up bar. Keeping legs straight or knees slightly bent, flex at hips and pull legs up until parallel to floor. Slowly lower back down, avoiding any swinging.",
                    defaultSets = 3,
                    defaultReps = 12,
                    defaultRestSeconds = 60
                )
            )
            fitnessDao.insertExercises(defaultExercises)

            // 3. Populate Workout Plans for all three goals
            val plans = mutableListOf<WorkoutPlan>()

            // --- Goal: Muscle Gain ---
            // Monday (Day 1): Chest & Arms
            plans.add(WorkoutPlan(dayOfWeek = 1, goal = "Muscle Gain", exerciseId = "bench_press", sets = 4, reps = 8, orderIndex = 0))
            plans.add(WorkoutPlan(dayOfWeek = 1, goal = "Muscle Gain", exerciseId = "dumbbell_fly", sets = 3, reps = 12, orderIndex = 1))
            plans.add(WorkoutPlan(dayOfWeek = 1, goal = "Muscle Gain", exerciseId = "bicep_curl", sets = 3, reps = 12, orderIndex = 2))
            plans.add(WorkoutPlan(dayOfWeek = 1, goal = "Muscle Gain", exerciseId = "tricep_pushdown", sets = 3, reps = 12, orderIndex = 3))

            // Tuesday (Day 2): Back & Core
            plans.add(WorkoutPlan(dayOfWeek = 2, goal = "Muscle Gain", exerciseId = "barbell_row", sets = 4, reps = 8, orderIndex = 0))
            plans.add(WorkoutPlan(dayOfWeek = 2, goal = "Muscle Gain", exerciseId = "pull_ups", sets = 4, reps = 10, orderIndex = 1))
            plans.add(WorkoutPlan(dayOfWeek = 2, goal = "Muscle Gain", exerciseId = "cable_crunch", sets = 3, reps = 15, orderIndex = 2))
            plans.add(WorkoutPlan(dayOfWeek = 2, goal = "Muscle Gain", exerciseId = "hanging_leg_raise", sets = 3, reps = 12, orderIndex = 3))

            // Thursday (Day 4): Legs
            plans.add(WorkoutPlan(dayOfWeek = 4, goal = "Muscle Gain", exerciseId = "barbell_squat", sets = 4, reps = 6, orderIndex = 0))
            plans.add(WorkoutPlan(dayOfWeek = 4, goal = "Muscle Gain", exerciseId = "romanian_deadlift", sets = 3, reps = 10, orderIndex = 1))

            // Friday (Day 5): Shoulders & Arms
            plans.add(WorkoutPlan(dayOfWeek = 5, goal = "Muscle Gain", exerciseId = "overhead_press", sets = 4, reps = 8, orderIndex = 0))
            plans.add(WorkoutPlan(dayOfWeek = 5, goal = "Muscle Gain", exerciseId = "lateral_raise", sets = 3, reps = 15, orderIndex = 1))
            plans.add(WorkoutPlan(dayOfWeek = 5, goal = "Muscle Gain", exerciseId = "bicep_curl", sets = 3, reps = 12, orderIndex = 2))
            plans.add(WorkoutPlan(dayOfWeek = 5, goal = "Muscle Gain", exerciseId = "tricep_pushdown", sets = 3, reps = 12, orderIndex = 3))


            // --- Goal: Strength ---
            // Monday (Day 1): Squat & Core
            plans.add(WorkoutPlan(dayOfWeek = 1, goal = "Strength", exerciseId = "barbell_squat", sets = 5, reps = 5, orderIndex = 0))
            plans.add(WorkoutPlan(dayOfWeek = 1, goal = "Strength", exerciseId = "hanging_leg_raise", sets = 3, reps = 10, orderIndex = 1))

            // Wednesday (Day 3): Bench & Shoulder Press
            plans.add(WorkoutPlan(dayOfWeek = 3, goal = "Strength", exerciseId = "bench_press", sets = 5, reps = 5, orderIndex = 0))
            plans.add(WorkoutPlan(dayOfWeek = 3, goal = "Strength", exerciseId = "overhead_press", sets = 4, reps = 6, orderIndex = 1))
            plans.add(WorkoutPlan(dayOfWeek = 3, goal = "Strength", exerciseId = "tricep_pushdown", sets = 3, reps = 8, orderIndex = 2))

            // Friday (Day 5): Pull & Deadlift
            plans.add(WorkoutPlan(dayOfWeek = 5, goal = "Strength", exerciseId = "romanian_deadlift", sets = 4, reps = 6, orderIndex = 0))
            plans.add(WorkoutPlan(dayOfWeek = 5, goal = "Strength", exerciseId = "barbell_row", sets = 4, reps = 6, orderIndex = 1))
            plans.add(WorkoutPlan(dayOfWeek = 5, goal = "Strength", exerciseId = "pull_ups", sets = 3, reps = 8, orderIndex = 2))


            // --- Goal: Fat Loss ---
            // Monday (Day 1): Full Body Power Split
            plans.add(WorkoutPlan(dayOfWeek = 1, goal = "Fat Loss", exerciseId = "barbell_squat", sets = 4, reps = 10, orderIndex = 0))
            plans.add(WorkoutPlan(dayOfWeek = 1, goal = "Fat Loss", exerciseId = "bench_press", sets = 4, reps = 10, orderIndex = 1))
            plans.add(WorkoutPlan(dayOfWeek = 1, goal = "Fat Loss", exerciseId = "barbell_row", sets = 4, reps = 10, orderIndex = 2))

            // Wednesday (Day 3): Full Body Volume Split
            plans.add(WorkoutPlan(dayOfWeek = 3, goal = "Fat Loss", exerciseId = "romanian_deadlift", sets = 3, reps = 12, orderIndex = 0))
            plans.add(WorkoutPlan(dayOfWeek = 3, goal = "Fat Loss", exerciseId = "overhead_press", sets = 3, reps = 12, orderIndex = 1))
            plans.add(WorkoutPlan(dayOfWeek = 3, goal = "Fat Loss", exerciseId = "pull_ups", sets = 3, reps = 10, orderIndex = 2))

            // Friday (Day 5): Metabolic Burn & Core
            plans.add(WorkoutPlan(dayOfWeek = 5, goal = "Fat Loss", exerciseId = "bicep_curl", sets = 3, reps = 15, orderIndex = 0))
            plans.add(WorkoutPlan(dayOfWeek = 5, goal = "Fat Loss", exerciseId = "tricep_pushdown", sets = 3, reps = 15, orderIndex = 1))
            plans.add(WorkoutPlan(dayOfWeek = 5, goal = "Fat Loss", exerciseId = "cable_crunch", sets = 4, reps = 20, orderIndex = 2))
            plans.add(WorkoutPlan(dayOfWeek = 5, goal = "Fat Loss", exerciseId = "lateral_raise", sets = 3, reps = 15, orderIndex = 3))

            fitnessDao.insertWorkoutPlans(plans)

            // 4. Insert Default Stats Log
            val now = System.currentTimeMillis()
            val dayInMillis = 24 * 60 * 60 * 1000L
            fitnessDao.insertBodyStat(BodyStatLog(weight = 81.5, bodyFat = 15.6, timestamp = now - 5 * dayInMillis))
            fitnessDao.insertBodyStat(BodyStatLog(weight = 81.0, bodyFat = 15.5, timestamp = now - 4 * dayInMillis))
            fitnessDao.insertBodyStat(BodyStatLog(weight = 80.6, bodyFat = 15.4, timestamp = now - 3 * dayInMillis))
            fitnessDao.insertBodyStat(BodyStatLog(weight = 80.3, bodyFat = 15.3, timestamp = now - 2 * dayInMillis))
            fitnessDao.insertBodyStat(BodyStatLog(weight = 80.1, bodyFat = 15.2, timestamp = now - 1 * dayInMillis))
            fitnessDao.insertBodyStat(BodyStatLog(weight = 80.0, bodyFat = 15.2, timestamp = now))

            // 5. Insert Default PR Workout Logs
            fitnessDao.insertWorkoutLog(WorkoutLog(exerciseId = "bench_press", exerciseName = "Barbell Bench Press", weight = 60.0, reps = 8, timestamp = now - 4 * dayInMillis, isPR = true))
            fitnessDao.insertWorkoutLog(WorkoutLog(exerciseId = "bench_press", exerciseName = "Barbell Bench Press", weight = 65.0, reps = 6, timestamp = now - 3 * dayInMillis, isPR = true))
            fitnessDao.insertWorkoutLog(WorkoutLog(exerciseId = "bench_press", exerciseName = "Barbell Bench Press", weight = 70.0, reps = 5, timestamp = now - 2 * dayInMillis, isPR = true))
            fitnessDao.insertWorkoutLog(WorkoutLog(exerciseId = "bench_press", exerciseName = "Barbell Bench Press", weight = 75.0, reps = 4, timestamp = now - 1 * dayInMillis, isPR = true))
        }
    }
}
