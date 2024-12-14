package com.example.dewy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dewy.data.models.Routine
import com.example.dewy.data.models.RoutineDay
import com.example.dewy.data.models.RoutineStep

import com.example.dewy.data.repositories.RoutineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class RoutineViewModel(
    private val repository: RoutineRepository = RoutineRepository()
) : ViewModel() {
    private val _routines = MutableStateFlow<List<Routine?>?>(null)
    val routines: StateFlow<List<Routine?>?> = _routines

    private val _todayRoutines = MutableStateFlow<List<RoutineDay?>?>(null)
    val todayRoutines: StateFlow<List<RoutineDay?>?> = _todayRoutines

    private suspend fun fetchRoutines() {
        val morning = repository.fetchRoutineByType("Morning")
        val evening = repository.fetchRoutineByType("Evening")
        _routines.value = listOf(morning, evening)
    }

    fun loadTodayRoutines() {
        viewModelScope.launch(Dispatchers.Main) {
            if (_routines.value == null) {
                fetchRoutines()
            }

            val today = LocalDate.now()
            val dayOfWeekIndex = (today.dayOfWeek.value - 1) % 7

            val todayMorningRoutine =
                _routines.value?.getOrNull(0)?.days?.getOrNull(dayOfWeekIndex)

            val todayEveningRoutine =
                _routines.value?.getOrNull(1)?.days?.getOrNull(dayOfWeekIndex)

            _todayRoutines.value = listOf(todayMorningRoutine, todayEveningRoutine)
        }
    }

    fun loadRoutines(){
        viewModelScope.launch(Dispatchers.Main) {
            fetchRoutines()
        }
    }

//
//    fun createRoutine(routineName: String, preferredTime: String, steps: List<Step>) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val success = repository.createRoutine(routineName, preferredTime, steps)
//            if (success) {
//                fetchRoutines()
//                _notification.value = "Routine created successfully!"
//            } else {
//                _notification.value = "Failed to create routine. Please try again."
//            }
//        }
//    }
//
//    fun updateRoutine(routineName: String, preferredTime: String, steps: List<Step>) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val success = repository.updateRoutine(routineName, preferredTime, steps)
//            if (success) {
//                fetchRoutines()
//                _notification.value = "Routine updated successfully!"
//            } else {
//                _notification.value = "Failed to update routine. Please try again."
//            }
//        }
//    }
//
//    fun deleteRoutine(routineName: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val success = repository.deleteRoutine(routineName)
//            if (success) {
//                fetchRoutines()
//                _notification.value = "Routine deleted successfully!"
//            } else {
//                _notification.value = "Failed to delete routine. Please try again."
//            }
//        }
//    }
}
