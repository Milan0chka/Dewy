package com.example.dewy.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dewy.R
import com.example.dewy.data.models.Streak
import com.example.dewy.data.models.Tip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.dewy.data.repositories.StreakRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Date


class StreakViewModel(
    private val repository: StreakRepository = StreakRepository()
) : ViewModel() {

    private val _streak = MutableStateFlow<Streak?>(null)
    val streak: StateFlow<Streak?> = _streak

    private val _streakDanger = MutableStateFlow(true)
    val streakDanger: StateFlow<Boolean> = _streakDanger

    init { fetchAndInitializeStreak() }

    private fun fetchAndInitializeStreak() {
        viewModelScope.launch(Dispatchers.Main) {
            val fetchedStreak = repository.fetchStreak()
            if (fetchedStreak == null) {
                resetStreak()
            } else {
                val (day, lastDate) = fetchedStreak
                val currentStreak = validateStreak(day, lastDate)

                if (currentStreak.first == 0) {
                    resetStreak()
                } else {
                    _streak.value = generateStreakInfo(currentStreak.first, currentStreak.second)
                    _streakDanger.value = isStreakInDanger(currentStreak.second)
                }
            }
        }
    }

    private fun validateStreak(day: Int, date: String): Pair<Int, String> {
        val currentDate = LocalDate.now().toString()
        val dayDifference = calculateDayDifference(date)

        return if (dayDifference in 0..1) {
            Pair(day, date)
        } else {
            Pair(0, currentDate)
        }
    }

    private fun calculateDayDifference(date: String): Int {
        val currentDate = LocalDate.now()
        val streakDate = LocalDate.parse(date)
        return ChronoUnit.DAYS.between(streakDate, currentDate).toInt()
    }

    private fun isStreakInDanger(date: String): Boolean {
        val dayDifference = calculateDayDifference(date)
        return dayDifference == 1
    }

    private fun resetStreak() {
        val currentDate = LocalDate.now().toString()
        val newStreak = generateStreakInfo(0, currentDate)

        _streak.value = newStreak
        _streakDanger.value = false

        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStreak(newStreak.num, newStreak.date)
        }
    }

    fun checkStreakStatus() {
        viewModelScope.launch(Dispatchers.Main) {
            _streak.value?.let { currentStreak ->
                val isDanger = isStreakInDanger(currentStreak.date)

                if (_streakDanger.value != isDanger)
                    _streakDanger.value = isDanger

                if (calculateDayDifference(currentStreak.date) > 1) {
                    resetStreak()
                }
            }
        }
    }

    private fun generateStreakInfo(num: Int, date: String): Streak {
        return when {
            num in 0..5 -> Streak(num, date, R.drawable.drop_stage1, "Good luck on your Dewy journey!")
            num in 6..10 -> Streak(num, date, R.drawable.drop_stage2, "Your skin is already thanking you!")
            num in 11..15 -> Streak(num, date, R.drawable.drop_stage3, "Consistency is key, and you're nailing it!")
            num in 16..20 -> Streak(num, date, R.drawable.drop_stage4, "Glow-up alert! Keep it going!")
            num in 21..25 -> Streak(num, date, R.drawable.drop_stage5, "Dewy dreams do come true!")
            num in 26..30 -> Streak(num, date, R.drawable.drop_stage6, "Glow mode: Activated!")
            else -> Streak(num, date, R.drawable.drop_stage7, "Glow so bright, you can replace the sun!")
        }
    }
}
