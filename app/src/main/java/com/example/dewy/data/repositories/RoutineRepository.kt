package com.example.dewy.data.repositories

import com.example.dewy.data.models.Product
import com.example.dewy.data.models.Routine
import com.example.dewy.data.models.RoutineDay
import com.example.dewy.data.models.RoutineStep
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RoutineRepository {
    private val db = FirebaseFirestore.getInstance()
    //cached routine to reduce number of fetches
    private val routineCache = mutableMapOf<String, Routine?>()

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

    private fun getCachedRoutine(type: String): Routine? {
        return routineCache[type]
    }

    private fun cacheRoutine(type: String, routine: Routine) {
        routineCache[type] = routine
    }

    fun clearCache(type: String) {
        routineCache.clear()
    }

    suspend fun fetchRoutineByType(type: String): Routine? {
        val cachedRoutine = getCachedRoutine(type)
        if (cachedRoutine != null) {
            println("Returning cached $type routine")
            return cachedRoutine
        }

        return try {
            val userId = fetchFirstUserId() ?: return null

            val routineSnapshot = db.collection("Users")
                .document(userId)
                .collection("Routines")
                .document(type)
                .get()
                .await()

            if (!routineSnapshot.exists()) return null

            val routineType = routineSnapshot.getString("type") ?: ""
            val preferredTime = routineSnapshot.getString("preferredTime") ?: ""

            val daysList = routineSnapshot.get("days") as? List<Map<String, Any>> ?: listOf()

            val days = daysList.map { dayMap ->
                val dayName = dayMap["dayName"] as? String ?: ""

                val stepsList = dayMap["steps"] as? List<Map<String, Any>> ?: listOf()

                val steps = stepsList.map { stepMap ->
                    val stepName = stepMap["stepName"] as? String ?: ""

                    val productsList = stepMap["products"] as? List<Map<String, Any>> ?: listOf()

                    val products = productsList.map { productMap ->
                        Product(
                            name = productMap["name"] as? String ?: "",
                            frequency = (productMap["frequency"] as? Long)?.toInt() ?: 1
                        )
                    }

                    RoutineStep(
                        stepName = stepName,
                        products = products.toMutableList()
                    )
                }

                RoutineDay(
                    dayName = dayName,
                    steps = steps
                )
            }

            val routine = Routine(
                type = routineType,
                preferredTime = preferredTime,
                days = days.toTypedArray()
            )

            cacheRoutine(type, routine)

            return routine
        } catch (e: Exception) {
            println("Error fetching routine: ${e.message}")
            null
        }
    }

    suspend fun fetchRoutineDay(type: String, dayName: String): RoutineDay? {
        try {
            val cachedRoutine = getCachedRoutine(type)
            if (cachedRoutine != null) {
                println("Returning $dayName from cached $type routine")
                return cachedRoutine.days.find { it.dayName == dayName }
            }

            val routine = fetchRoutineByType(type)

            if (routine != null) {
                return routine.days.find { it.dayName == dayName }
            } else {
                return null
            }
        } catch (e: Exception) {
            println("Error fetching routine day: ${e.message}")
            return null
        }
    }

    suspend fun saveRoutine(routine: Routine): Boolean {
        return try {
            val userId = fetchFirstUserId() ?: return false

            val routineRef = db.collection("Users")
                .document(userId)
                .collection("Routines")
                .document(routine.type)

            val routineMap = linkedMapOf(
                "type" to routine.type,
                "preferredTime" to routine.preferredTime,
                "days" to routine.days.map { day ->
                    linkedMapOf(
                        "dayName" to day.dayName,
                        "steps" to day.steps.map { step ->
                            linkedMapOf(
                                "stepName" to step.stepName,
                                "products" to step.products.map { product ->
                                    linkedMapOf(
                                        "name" to product.name,
                                        "activeIngredients" to product.activeIngredients
                                    )
                                }
                            )
                        }
                    )
                }
            )

            routineRef.set(routineMap).await()

            true
        } catch (e: Exception) {
            println("Error saving routine: ${e.message}")
            false
        }
    }

    suspend fun deleteRoutine(type: String): Boolean {
        return try {
            val userId = fetchFirstUserId() ?: return false

            val routineRef = db.collection("Users")
                .document(userId)
                .collection("Routines")
                .document(type)

            val daysSnapshot = routineRef.collection("Days").get().await()
            daysSnapshot.documents.forEach { dayDoc ->
                val stepsSnapshot = dayDoc.reference.collection("Steps").get().await()
                stepsSnapshot.documents.forEach { it.reference.delete().await() }
                dayDoc.reference.delete().await()
            }

            routineRef.delete().await()
            true
        } catch (e: Exception) {
            println("Error deleting routine: ${e.message}")
            false
        }
    }
}
