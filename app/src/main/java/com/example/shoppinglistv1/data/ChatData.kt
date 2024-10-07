package com.example.shoppinglistv1.data

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ChatData {

    val api_key = "AIzaSyCC_it7ZkPSDXVf-LE9iqusH1fnEAyLyRI"

    suspend fun getResponse(prompt: String): Chat {
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = api_key,
        )
        try {
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }
            return Chat(
                prompt = response.text ?: "error",
                isFromUser = false
            )
        } catch (e: ResponseStoppedException) {
            return Chat(
                prompt = e.message ?: "error",
                isFromUser = false
            )
        }
    }
}