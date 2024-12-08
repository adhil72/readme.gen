package service

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import modals.GeminiResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class Gemini(
    private val apiKey: String ,
    private val sysInstruction: String = "",
    private val model: String = GEMINI_FLASH
) {

    private val client =
        OkHttpClient.Builder().connectTimeout(200, TimeUnit.SECONDS).writeTimeout(200, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.MINUTES).build()

    private val gson = Gson()
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta"

    companion object {
        val GEMINI_PRO = "gemini-1.5-pro"
        val GEMINI_FLASH = "gemini-1.5-flash"
        val GEMINI_FLASH_8b = "gemini-1.5-flash-8b"
        val GEMNI_EXP = "learnlm-1.5-pro-experimental"
        val GEMNI_EXP_2 = "gemini-exp-1206"
    }

    fun generateResponse(prompt: String): String {
        val url = "$baseUrl/models/$model:generateContent?key=$apiKey"

        val body = mapOf(
            "contents" to listOf(
                mapOf(
                    "role" to "user", "parts" to listOf(
                        mapOf(
                            "text" to prompt
                        )
                    )
                )
            ), "generationConfig" to mapOf(
                "temperature" to 1,
                "topK" to 64,
                "topP" to 0.95,
                "maxOutputTokens" to 8192,
                "responseMimeType" to "text/plain"
            ), "systemInstruction" to mapOf(
                "role" to "user", "parts" to listOf(
                    mapOf(
                        "text" to sysInstruction
                    )
                )
            )
        )

        val requestBody = gson.toJson(body).toRequestBody("application/json".toMediaType())

        val request = Request.Builder().url(url).post(requestBody).addHeader("Content-Type", "application/json").build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseString = GeminiResponse.parse(response.body?.string() ?: "{}")
            return responseString.candidates[0].content.parts[0].text
        }

        return " "
    }

    suspend fun createSession(): SessionResponse? = withContext(Dispatchers.IO) {
        val url = "$baseUrl/models/learnlm-1.5-pro-experimental:createSession?key=$apiKey"
        val requestBody = """
            {
                "model":"models/learnlm-1.5-pro-experimental"
            }
        """.trimIndent().toRequestBody("application/json".toMediaType())

        val request = Request.Builder().url(url).post(requestBody).build()

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            return@withContext gson.fromJson(response.body?.string(), SessionResponse::class.java)

        } else {

            return@withContext null
        }
    }
}

data class GenerateContentResponse(val candidates: List<Candidate>?)

data class Candidate(val content: String?)

data class SessionResponse(
    val name: String
)