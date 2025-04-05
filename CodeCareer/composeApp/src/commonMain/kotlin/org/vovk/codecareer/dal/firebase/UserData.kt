package org.vovk.codecareer.dal.firebase

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.browser.localStorage
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class UserData(
    val uid: String,
    val displayName: String? = null,
    val email: String? = null,
    val photoURL: String? = null,
    val success: Boolean = true
)

object UserSessionManager {
    private const val USER_SESSION_KEY = "user_session"
    private val json = Json { ignoreUnknownKeys = true }
    
    // Observable state for current user
    var currentUser by mutableStateOf<UserData?>(null)
        private set
    
    init {
        // Try to load user from localStorage on initialization
        loadUserFromStorage()
    }
    
    // Store user data in session
    fun saveUserSession(userDataJson: String) {
        try {
            val userData = json.decodeFromString<UserData>(userDataJson)
            currentUser = userData

            // Save to localStorage for persistence
            localStorage.setItem(USER_SESSION_KEY, userDataJson)
            
            println("User session saved: ${userData.displayName}")
        } catch (e: Exception) {
            println("Error saving user session: ${e.message}")
        }
    }
    
    // Load user from localStorage
    private fun loadUserFromStorage() {
        try {
            val storedUser = localStorage.getItem(USER_SESSION_KEY)
            if (storedUser != null) {
                currentUser = json.decodeFromString(storedUser)
                println("User loaded from storage: ${currentUser?.displayName}")
            }
        } catch (e: Exception) {
            println("Error loading user from storage: ${e.message}")
        }
    }
    
    // Clear the user session (for logout)
    fun clearUserSession() {
        currentUser = null
        localStorage.removeItem(USER_SESSION_KEY)
        println("User session cleared")
    }
    
    // Check if user is logged in
    fun isLoggedIn(): Boolean {
        return currentUser != null
    }
}