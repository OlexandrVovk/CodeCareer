package com.taktyka.c2.dal.utils.multiLangSupport

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

external fun getEnJsonData(): String
external fun loadEnLanguage(): String

external fun getUkJsonData(): String
external fun loadUkLanguage(): String

object Strings {
    var currentLanguage by mutableStateOf("en")
    private var translations by mutableStateOf<JsonObject?>(null)

    init {
        CoroutineScope(Dispatchers.Default).launch {
            loadLanguage(currentLanguage)
        }
    }

    fun loadLanguage(language: String) {
        currentLanguage = language
        when (language) {
            "en" -> {
                loadEnLanguage()
                translations = Json.parseToJsonElement(getEnJsonData()).jsonObject
            }
            "uk" -> {
                loadUkLanguage()
                translations = Json.parseToJsonElement(getUkJsonData()).jsonObject
            }
        }
    }

    private fun getString(key: String): String {
        return (translations?.get(key) as? JsonPrimitive)?.content ?: key
    }

    @Composable
    fun text(text: String): String {
        return getString(text)
    }
}
