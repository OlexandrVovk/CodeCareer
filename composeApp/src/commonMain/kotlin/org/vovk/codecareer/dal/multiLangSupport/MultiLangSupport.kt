package com.taktyka.c2.dal.utils.multiLangSupport

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

external fun getJsonData(): String
external fun loadEnLanguage(): String

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
        loadEnLanguage()
        translations = try {
            val test = Json.parseToJsonElement(getJsonData()).jsonObject
            println(test.toString())
            test
        } catch (e: Exception) {
            //console.error("Error parsing JSON for $language: ${e.message}")
            null
        }
    }

    fun getString(key: String): String {
        return (translations?.get(key) as? JsonPrimitive)?.content ?: key
    }

    @Composable
    fun text(text: String): String {
        return getString(text)
    }
}
