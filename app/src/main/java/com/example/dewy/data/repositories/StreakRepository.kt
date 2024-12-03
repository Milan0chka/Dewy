package com.example.dewy.data.repositories

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.dewy.data.models.Tip
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class StreakRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchStreak(): Pair<Int, String>? {
        return try {
            val snapshot = db.collection("Users").limit(1).get().await()
            if (!snapshot.isEmpty) {
                val document = snapshot.documents[0]
                val streak = document.get("streak") as? Map<String, Any>
                println("Fetched Streak Data: $streak")

                val day = (streak?.get("day") as? Long)?.toInt()
                val lastDate = streak?.get("last_date") as? String

                if (day != null && lastDate != null) Pair(day, lastDate) else null
            } else null
        } catch (e: Exception) {
            println("Error Fetching Streak: ${e.message}")
            null
        }
    }

    suspend fun updateStreak(day: Int, date: String) {
        try {
            val snapshot = db.collection("Users").limit(1).get().await()
            if (!snapshot.isEmpty) {
                val document = snapshot.documents[0]

                val streakData = mapOf(
                    "day" to day,
                    "last_date" to date
                )

                db.collection("Users")
                    .document(document.id)
                    .update("streak", streakData)
                    .await()

                println("Streak Updated: Day=$day, Last Date=$date")
            } else {
                println("No document found to update.")
            }
        } catch (e: Exception) {
            println("Error Updating Streak: ${e.message}")
        }
    }


}