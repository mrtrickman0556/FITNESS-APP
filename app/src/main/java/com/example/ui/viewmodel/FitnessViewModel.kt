package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class FitnessViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FitnessRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = FitnessRepository(database.fitnessDao())
        
        // Populate default data if empty on startup
        viewModelScope.launch {
            repository.prepopulateIfNeeded()
        }
    }

    // --- State Observables ---
    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .map { it ?: UserProfile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val allExercises: StateFlow<List<Exercise>> = repository.allExercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWorkoutLogs: StateFlow<List<WorkoutLog>> = repository.allWorkoutLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBodyStats: StateFlow<List<BodyStatLog>> = repository.allBodyStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMealLogs: StateFlow<List<MealLog>> = repository.allMealLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRecoveryLogs: StateFlow<List<RecoveryLog>> = repository.allRecoveryLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Filters & Today's Plan ---
    private val _selectedDayOfWeek = MutableStateFlow(getCurrentDayOfWeek())
    val selectedDayOfWeek: StateFlow<Int> = _selectedDayOfWeek.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val todayWorkoutPlans: StateFlow<List<WorkoutPlan>> = combine(
        selectedDayOfWeek,
        userProfile
    ) { day, profile ->
        Pair(day, profile.goal)
    }.flatMapLatest { (day, goal) ->
        repository.getPlanForDay(day, goal)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Exercises
    private val _selectedMuscleGroup = MutableStateFlow<String?>(null)
    val selectedMuscleGroup: StateFlow<String?> = _selectedMuscleGroup.asStateFlow()

    private val _selectedEquipment = MutableStateFlow<String?>(null)
    val selectedEquipment: StateFlow<String?> = _selectedEquipment.asStateFlow()

    val filteredExercises: StateFlow<List<Exercise>> = combine(
        allExercises,
        _selectedMuscleGroup,
        _selectedEquipment
    ) { list, muscle, equip ->
        var result = list
        if (muscle != null) {
            result = result.filter { it.muscleGroup.equals(muscle, ignoreCase = true) }
        }
        if (equip != null) {
            result = result.filter { it.equipment.equals(equip, ignoreCase = true) }
        }
        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's nutrition totals
    val todayMealLogs: StateFlow<List<MealLog>> = repository.getTodayMealLogs(getStartOfToday())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayCalories: StateFlow<Int> = todayMealLogs
        .map { meals -> meals.sumOf { it.calories } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayProtein: StateFlow<Int> = todayMealLogs
        .map { meals -> meals.sumOf { it.protein } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // --- Active Workout State & Logging Session ---
    private val _activeExercise = MutableStateFlow<Exercise?>(null)
    val activeExercise: StateFlow<Exercise?> = _activeExercise.asStateFlow()

    private val _loggedSetsInSession = MutableStateFlow<List<WorkoutLog>>(emptyList())
    val loggedSetsInSession: StateFlow<List<WorkoutLog>> = _loggedSetsInSession.asStateFlow()

    // --- Actions & Methods ---
    fun selectDay(day: Int) {
        _selectedDayOfWeek.value = day
    }

    suspend fun getExerciseById(id: String): Exercise? {
        return repository.getExerciseById(id)
    }

    fun setMuscleGroupFilter(muscle: String?) {
        _selectedMuscleGroup.value = muscle
    }

    fun setEquipmentFilter(equip: String?) {
        _selectedEquipment.value = equip
    }

    // Update profile
    fun updateProfile(goal: String, weight: Double, calories: Int, protein: Int, experience: String) {
        viewModelScope.launch {
            val updated = UserProfile(
                id = 1,
                goal = goal,
                weightKg = weight,
                targetCalories = calories,
                targetProtein = protein,
                experienceLevel = experience
            )
            repository.saveUserProfile(updated)
            // Log body stat as weight tracking entry
            repository.logBodyStat(BodyStatLog(weight = weight, bodyFat = 15.0))
        }
    }

    // Active Exercise Navigation
    fun startExerciseLogging(exercise: Exercise) {
        _activeExercise.value = exercise
        _loggedSetsInSession.value = emptyList()
    }

    fun finishExerciseLogging() {
        _activeExercise.value = null
        _loggedSetsInSession.value = emptyList()
    }

    // Log workout set
    fun logSet(weight: Double, reps: Int) {
        val exercise = _activeExercise.value ?: return
        viewModelScope.launch {
            // Check if this is a PR
            val maxWeightFlow = repository.getMaxWeightForExercise(exercise.id)
            val currentMax = maxWeightFlow.firstOrNull() ?: 0.0
            val isPR = weight > currentMax && currentMax > 0.0

            val logEntry = WorkoutLog(
                exerciseId = exercise.id,
                exerciseName = exercise.name,
                weight = weight,
                reps = reps,
                isPR = isPR
            )
            repository.logWorkout(logEntry)

            // Keep track of sets logged in this exact screen session
            _loggedSetsInSession.value = _loggedSetsInSession.value + logEntry
        }
    }

    fun deleteLog(id: Int) {
        viewModelScope.launch {
            repository.deleteWorkoutLog(id)
        }
    }

    // Log stats
    fun logWeight(weight: Double, bodyFat: Double) {
        viewModelScope.launch {
            repository.logBodyStat(BodyStatLog(weight = weight, bodyFat = bodyFat))
            // Also update current profile weight
            val current = userProfile.value
            repository.saveUserProfile(current.copy(weightKg = weight))
        }
    }

    // Log meals
    fun logMeal(name: String, calories: Int, protein: Int) {
        viewModelScope.launch {
            repository.logMeal(MealLog(mealName = name, calories = calories, protein = protein))
        }
    }

    fun deleteMeal(id: Int) {
        viewModelScope.launch {
            repository.deleteMealLog(id)
        }
    }

    // Log recovery
    fun logRecovery(sleep: Double, stretching: Boolean) {
        viewModelScope.launch {
            repository.logRecovery(RecoveryLog(sleepHours = sleep, stretchingCompleted = stretching))
        }
    }

    // --- Helper calculations ---
    fun getCurrentDayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1
        }
    }

    private fun getStartOfToday(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
