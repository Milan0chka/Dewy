package com.example.dewy.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dewy.services.UVIndexService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UVIndexViewModel : ViewModel() {

    private val _uvIndex = MutableStateFlow<Double?>(null)
    val uvIndex: StateFlow<Double?> = _uvIndex

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location

    private val service = UVIndexService()

    fun setLocation(location: Location?) {
        _location.value = location
    }

    fun fetchUVIndex() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (_location.value == null) {
                    println("Location is null, cannot fetch UV Index.")
                    return@launch
                }

                val uvIndex = service.fetchUVIndex(_location.value!!.latitude, _location.value!!.longitude)
                _uvIndex.value = uvIndex
                println("Successfully fetched UV Index: $uvIndex")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error fetching UV Index: ${e.message}")
            }
        }
    }
}
