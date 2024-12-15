package com.example.dewy.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class UVIndexResponse(
    val now: NowData
)

@Serializable
data class NowData(
    val uvi: Double
)

class UVIndexService {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    suspend fun fetchUVIndex(lat: Double, lon: Double): Double? {
        return try {
            val response: UVIndexResponse = client.get("https://currentuvindex.com/api/v1/uvi?latitude=$lat&longitude=$lon").body()
            response.now.uvi
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error fetching UV Index: ${e.message}")
            null
        }
    }
}
