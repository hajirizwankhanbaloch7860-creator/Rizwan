package com.example.network

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {

    private const val TAG = "GeminiClient"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    // Default system instruction to configure the model as a highly polite, respectful, and scholarly AI Islamic Companion
    private const val DEFAULT_SYSTEM_INSTRUCTION = """
        You are a highly polite, respectful, and wise AI Islamic Companion in the "Al Quran & Hadith" app.
        Your purpose is to assist users in understanding Islamic concepts, Quranic verses, and authentic Hadiths.
        
        Guidelines:
        1. Always begin your answer with a warm, respectful Islamic greeting (e.g., "Assalamu Alaikum", "Bismillah").
        2. Answer queries strictly using and citing verified Quranic Surahs/Ayahs and authentic Hadiths (Sahih al-Bukhari, Sahih Muslim, etc.) where possible.
        3. Explain meanings clearly in a friendly, conversational tone.
        4. Support English, Urdu, and Arabic languages depending on the user's prompt language.
        5. Respect scholarly difference of opinion, maintain a neutral, humble tone, and always end with a supplication or polite closing.
        6. Do not make up any verses or Hadiths. If you do not know the answer, politely state so.
    """

    suspend fun askAssistant(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key is not configured or holds a placeholder value.")
            return@withContext "API Key Configuration Required: Please configure your GEMINI_API_KEY in the Secrets panel in Google AI Studio to use the AI Islamic Assistant."
        }

        val url = "$BASE_URL/$MODEL_NAME:generateContent?key=$apiKey"

        try {
            // Build the standard JSON payload using Android's built-in JSONObject (ultra robust, no external serializator mismatch risks)
            val root = JSONObject()
            
            // Contents
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            val partObj = JSONObject()
            partObj.put("text", prompt)
            partsArray.put(partObj)
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            root.put("contents", contentsArray)

            // System Instruction
            val systemInstructionObj = JSONObject()
            val systemPartsArray = JSONArray()
            val systemPartObj = JSONObject()
            systemPartObj.put("text", DEFAULT_SYSTEM_INSTRUCTION)
            systemPartsArray.put(systemPartObj)
            systemInstructionObj.put("parts", systemPartsArray)
            root.put("systemInstruction", systemInstructionObj)

            val requestBodyJson = root.toString()
            val requestBody = requestBodyJson.toRequestBody(JSON_MEDIA_TYPE)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Content-Type", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "Request failed: code = ${response.code}, body = $errBody")
                    return@withContext "Submitting request failed. Code: ${response.code}. Please ensure your API key in the AI Studio secrets is active and correct."
                }

                val responseBodyStr = response.body?.string() ?: ""
                val responseJson = JSONObject(responseBodyStr)
                
                // Parse out the candidate text
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    if (content != null) {
                        val parts = content.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            return@withContext parts.getJSONObject(0).optString("text", "No text found in response parts.")
                        }
                    }
                }
                
                return@withContext "Received an empty response from the assistant. Please try rephrasing your question."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini API request", e)
            return@withContext "Unable to reach the AI Islamic Assistant. Error: ${e.localizedMessage ?: "Unknown network exception"}. Please verify your internet connection."
        }
    }
}
