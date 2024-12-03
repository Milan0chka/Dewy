package com.example.dewy.data.repositories

import com.example.dewy.data.models.Tip
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TipRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchRandomTip(): Tip? {
        return try {
            val countSnapshot = db.collection("Tips").get().await()
            val totalCount = countSnapshot.size()

            if (totalCount > 0) {
                val randomIndex = (0 until totalCount).random()
                val document = countSnapshot.documents[randomIndex]
                Tip(
                    content = document.getString("content") ?: "No content",
                    category = document.getString("category") ?: "General"
                )
            }
            else null
        } catch (e: Exception) {
            null
        }
    }
}