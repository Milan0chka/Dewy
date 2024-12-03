package com.example.dewy.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dewy.data.models.Record
import com.example.dewy.data.models.Streak
import com.example.dewy.data.repositories.RecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.w3c.dom.Comment
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class JournalViewModel(
    private val repository: RecordRepository = RecordRepository()
) :ViewModel() {
    private val _tags = MutableStateFlow<List<String>?>(null)
    val tags: StateFlow<List<String>?> = _tags

    private val _records = MutableStateFlow<List<Record>?>(null)
    val records: StateFlow<List<Record>?> = _records

    private val _dayDifference = MutableStateFlow<Int>(0)
    val dayDifference: StateFlow<Int> = _dayDifference

    private val _notification = MutableStateFlow<String?>(null)
    val notification: StateFlow<String?> = _notification

    init {
        fetchImprovementTags()
        checkRecordDateDifference()
    }

    private fun fetchImprovementTags() {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedTags = repository.fetchImprovementTags()

            val capitalizedTags = fetchedTags.map { tag ->
                tag.lowercase().replaceFirstChar { it.uppercase() }
            }

            _tags.value = capitalizedTags
        }
    }


    fun fetchUserRecords() {
        viewModelScope.launch(Dispatchers.Main) {
            val fetchedRecords = repository.fetchAllRecords()
            _records.value = fetchedRecords
        }
    }

    private fun saveImage(context: Context, bitmap: Bitmap): Boolean {
        return try {
            val currentDate = LocalDate.now().toString()
            val fileName = "record_$currentDate.jpeg"
            val file = File(context.filesDir, fileName)

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
            Log.d("SaveImage", "Image saved at: ${file.absolutePath}")
            true
        } catch (e: Exception) {
            Log.e("SaveImage", "Error saving image: ${e.message}")
            false
        }
    }



    fun addRecord(
        context: Context,
        comment: String,
        tags: List<String>,
        bitmap: Bitmap?,
        onResult: (Boolean) -> Unit
    ) {
        if (bitmap == null) {
            _notification.value = "Please upload a photo before saving the record."
            onResult(false)
            return
        }

        if (!saveImage(context, bitmap)) {
            _notification.value = "Failure to save photo. Please try again later."
            onResult(false)
            return
        }

        val currentDate = LocalDate.now().toString()

        val newRecord = Record(
            date = currentDate,
            image_url = "record_$currentDate.jpeg",
            comment = comment.ifEmpty { null },
            tags = tags
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = repository.addRecord(newRecord)
                if (success) {
                    fetchUserRecords()
                    onResult(true)
                } else {
                    _notification.value = "Failed to save the record. Please try again."
                    onResult(false)
                }
            } catch (e: Exception) {
                _notification.value = "An error occurred: ${e.message}"
                onResult(false)
            }
        }
    }


    fun deleteRecord(recordDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.deleteRecord(recordDate)
            if (success) {
                fetchUserRecords()
                _notification.value = "Record was deleted!"
            } else {
                _notification.value = "Failed to delete the record. Please try again."
            }
        }
    }

    private fun checkRecordDateDifference() {
        viewModelScope.launch(Dispatchers.IO) {
            val latestRecordDate = repository.fetchLatestRecordDate()
            if (latestRecordDate == null) {
                _dayDifference.value = 7
            } else {
                val currentDate = LocalDate.now()
                val lastDate = LocalDate.parse(latestRecordDate)
                val difference = ChronoUnit.DAYS.between(lastDate, currentDate).toInt()
                _dayDifference.value = difference
            }
        }
    }

}