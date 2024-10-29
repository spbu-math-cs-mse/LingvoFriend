package com.example.lingvofriend.llmApi

// based on YaGpt response JSON example

data class PromptResponse(
    val result: PromptResult,
)

data class PromptResult(
    val alternatives: List<Alternatives>,
    val usage: Usage,
    val modelVersion: String,
)

data class Alternatives(
    val message: Message,
    val status: String,
)

data class Usage(
    val inputTextTokens: String,
    val completionTokens: String,
    val totalTokens: String,
)
