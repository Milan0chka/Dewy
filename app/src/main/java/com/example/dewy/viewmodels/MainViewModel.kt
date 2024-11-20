package com.example.dewy.viewmodels

import android.util.Log
import androidx.compose.animation.core.snap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dewy.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

data class Tip(
    val content: String = "",
    val category: String = ""
)

data class Streak(
    val num: Int = 0,
    val image: Int = 0,
    val msg: String = ""
)

class MainViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _tip = MutableStateFlow<Tip>(Tip("", ""))
    val tip: StateFlow<Tip> = _tip
    private val _streak = MutableStateFlow<Streak>(Streak(0,R.drawable.drop_stage1,""))
    val streak: StateFlow<Streak> = _streak

    init {
        fetchRandomTip()
        fetchStreakNumber()
    }

    fun fetchRandomTip() {
        viewModelScope.launch {
            try {
                // Step 1: Fetch the total count of documents
                val countSnapshot = db.collection("Tips").get().await()
                val totalCount = countSnapshot.size()

                if (totalCount > 0) {
                    // Step 2: Generate a random index
                    val randomIndex = (0 until totalCount).random()

                    // Step 3: Get all documents and pick the random one
                    val document = countSnapshot.documents[randomIndex]
                    val tipContent = document.getString("content") ?: "No content"
                    val tipCategory = document.getString("category") ?: "General"

                    // Update the state with the random tip
                    _tip.value = Tip(
                        content = tipContent,
                        category = tipCategory
                            .lowercase()
                            .replaceFirstChar { it.uppercase() }
                            .replace('_', ' ')
                    )

                    Log.d("MainViewModel", "Fetched Random Tip: $tipContent, Category: $tipCategory")
                } else {
                    // If no tips are available
                    _tip.value = Tip(content = "No tips available.", category = "General")
                    Log.e("MainViewModel", "No documents found in the 'Tips' collection.")
                }
            } catch (e: Exception) {
                // Handle errors
                Log.e("MainViewModel", "Error fetching random tip: ${e.message}")
                _tip.value = Tip(content = "Error loading tip.", category = "Error")
            }
        }
    }

    fun fetchStreakNumber() {
        viewModelScope.launch {
            Log.d("MainViewModel", "Starting fetchStreakNumber()")
            try {
                // Firestore query
                val snapshot = db.collection("Users").limit(1).get().await()
                Log.d("MainViewModel", "Snapshot Retrieved: ${snapshot.documents}")

                if (!snapshot.isEmpty) {
                    val document = snapshot.documents[0]
                    Log.d("MainViewModel", "Document Retrieved: $document")

                    // Parse streak value
                    val streak = document.getString("streak")?.toIntOrNull() ?: 0
                    Log.d("MainViewModel", "Parsed Streak: $streak")

                    // Update state
                    _streak.value = generateStreakInfo(streak)
                    Log.d("MainViewModel", "Streak Updated: ${_streak.value}")
                } else {
                    _streak.value = Streak(0, R.drawable.drop_stage1, "No streak available.")
                    Log.e("MainViewModel", "No documents found in the 'Users' collection.")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching streak info: ${e.message}")
                _streak.value = Streak(0, R.drawable.drop_stage1, "Error fetching streak.")
            }
            Log.d("MainViewModel", "Completed fetchStreakNumber()")
        }
    }

    fun generateStreakInfo(num: Int): Streak{
        return when {
            num in 0..5 -> Streak(
                num = num,
                image = R.drawable.drop_stage1, // Replace with your actual drawable resource
                msg = "Good luck on your Dewy journey!"
            )
            num in 6..10 -> Streak(
                num = num,
                image = R.drawable.drop_stage2,
                msg = "Your skin is already thanking you!"
            )
            num in 11..15 -> Streak(
                num = num,
                image = R.drawable.drop_stage3,
                msg = "Consistency is key, and you're nailing it!"
            )
            num in 16..20 -> Streak(
                num = num,
                image = R.drawable.drop_stage4,
                msg = "Glow-up alert! Keep it going!"
            )
            num in 21..25 -> Streak(
                num = num,
                image = R.drawable.drop_stage5,
                msg = "Dewy dreams do come true!"
            )
            num in 26..30 -> Streak(
                num = num,
                image = R.drawable.drop_stage6,
                msg = "Glow mode: Activated!"
            )
            else -> Streak(
                num = num,
                image = R.drawable.drop_stage7,
                msg = "Glow so bright, you're lighting up the world!"
            )
        }
    }
}