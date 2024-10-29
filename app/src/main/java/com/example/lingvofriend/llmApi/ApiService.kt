package com.example.lingvofriend.llmApi

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

// form for http request
interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("completion")
    fun sendPrompt(
        @Header("Authorization") apiKey: String,
        @Header("x-folder-id") folderId: String,
        @Body prompt: PromptRequest,
    ): Call<PromptResponse>
}

// function to build client and send request async
suspend fun buildClient(chatMessages: List<Message>): String =
    withContext(Dispatchers.IO) {
        try {
            // initializing OkHttpClient
            val client = OkHttpClient.Builder().build()

            // initializing retrofit
            val retrofit =
                Retrofit
                    .Builder()
                    .baseUrl("https://llm.api.cloud.yandex.net/foundationModels/v1/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            // setting up a request
            val apiService = retrofit.create(ApiService::class.java)

            val apiKey = "Api-Key AQVN07OQURQ4oCSX6VeG_8-cKQXOibWdO0dpClQx"
            val folderId = "b1gq1u711tpqm1mskqc5"

            // forming a PromptRequest
            val prompt =
                PromptRequest(
                    modelUri = "gpt://b1gq1u711tpqm1mskqc5/yandexgpt-lite",
                    completionOptions =
                        CompletionOptions(
                            stream = false,
                            temperature = 0.6,
                            maxTokens = "2000",
                        ),
                    messages = chatMessages,
                )

            // sending request
            val response = apiService.sendPrompt(apiKey, folderId, prompt).execute()

            // forming a response
            if (response.isSuccessful) {
                response
                    .body()
                    ?.result
                    ?.alternatives
                    ?.get(0)
                    ?.message
                    ?.text ?: "No response text"
            } else {
                "Error: ${response.errorBody()?.string()}"
            }
        } catch (e: Exception) {
            "Request failed: ${e.message}"
        }
    }