package com.example.dewy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dewy.data.models.Routine
import com.example.dewy.data.models.RoutineStep

import com.example.dewy.data.repositories.RoutineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RoutineViewModel(
    private val repository: RoutineRepository = RoutineRepository()
) : ViewModel() {

    private val _routines = MutableStateFlow<List<Routine>?>(null)
    val routines: StateFlow<List<Routine>?> = _routines

    private val _notification = MutableStateFlow<String?>(null)
    val notification: StateFlow<String?> = _notification

    fun fetchRoutines() {
        viewModelScope.launch(Dispatchers.IO) {
            val morningRoutine = repository.fetchRoutineByType("Morning")
            val eveningRoutine = repository.fetchRoutineByType("Evening")

            val routinesList = listOfNotNull(morningRoutine, eveningRoutine)

            _routines.value = if (routinesList.isEmpty()) emptyList() else routinesList
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
