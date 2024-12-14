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

    private val _morningRoutine = MutableStateFlow<Routine?>(null)
    val morningRoutine: StateFlow<Routine?> = _morningRoutine

    private val _eveningRoutines = MutableStateFlow<Routine?>(null)
    val eveningRoutine: StateFlow<Routine?> = _eveningRoutines

    private val _notification = MutableStateFlow<String?>(null)
    val notification: StateFlow<String?> = _notification

    fun fetchRoutines() {
        viewModelScope.launch(Dispatchers.IO) {
            _morningRoutine.value = repository.fetchRoutineByType("Morning")
            _eveningRoutines.value = repository.fetchRoutineByType("Evening")
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
