package com.example.dewy.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.example.dewy.data.models.Record
import kotlinx.coroutines.tasks.await

class RecordRepository {
    private val db = FirebaseFirestore.getInstance()

    private suspend fun fetchFirstUserId(): String? {
        return try {
            val snapshot = db.collection("Users").limit(1).get().await()
            if (!snapshot.isEmpty) {
                snapshot.documents[0].id
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error fetching first user ID: ${e.message}")
            null
        }
    }

    suspend fun fetchImprovementTags(): List<String> {
        return try {
            val snapshot = db.collection("ImprovementTags").get().await()
            snapshot.documents.mapNotNull { it.getString("name") }
        } catch (e: Exception) {
            println("Error fetching improvement tags: ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchAllRecords(): List<Record> {
        return try {
            val userId = fetchFirstUserId()
            if (userId != null) {
                val snapshot = db.collection("Users")
                    .document(userId)
                    .collection("Records")
                    .get()
                    .await()

                snapshot.documents.map { doc ->
                    Record(
                        date = doc.getString("date") ?: "",
                        image_url = doc.getString("image_url") ?: "",
                        comment = doc.getString("comment") ?: "",
                        tags = doc.get("tags") as? List<String> ?: emptyList()
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            println("Error fetching records: ${e.message}")
            emptyList()
        }
    }

    suspend fun addRecord(record: Record): Boolean {
        return try {
            val userId = fetchFirstUserId()
            if (userId != null) {
                db.collection("Users")
                    .document(userId)
                    .collection("Records")
                    .add(record)
                    .await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            println("Error adding record: ${e.message}")
            false
        }
    }

    suspend fun deleteRecord(recordDate: String): Boolean {
        return try {
            val userId = fetchFirstUserId()
            if (userId != null) {
                val recordSnapshot = db.collection("Users")
                    .document(userId)
                    .collection("Records")
                    .whereEqualTo("date", recordDate)
                    .get()
                    .await()

                if (!recordSnapshot.isEmpty) {
                    for (doc in recordSnapshot.documents) {
                        db.collection("Users")
                            .document(userId)
                            .collection("Records")
                            .document(doc.id)
                            .delete()
                            .await()
                    }
                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            println("Error deleting record: ${e.message}")
            false
        }
    }

    suspend fun fetchLatestRecordDate(): String? {
        return try {
            val userId = fetchFirstUserId()
            if (userId != null) {
                val recordSnapshot = db.collection("Users")
                    .document(userId)
                    .collection("Records")
                    .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .await()

                recordSnapshot.documents.firstOrNull()?.getString("date")
            } else {
                println("No user found.")
                null
            }
        } catch (e: Exception) {
            println("Error fetching latest record date: ${e.message}")
            null
        }
    }

}
