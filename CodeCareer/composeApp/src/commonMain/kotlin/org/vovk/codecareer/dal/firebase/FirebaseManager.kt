package org.vovk.codecareer.dal.firebase

import org.vovk.codecareer.dal.entities.JobCartEntity

// Auth JS functions declaration
external fun createUserWithEmail(email: String, password: String, displayName: String, callback: (String) -> Unit)
external fun signInWithEmail(email: String, password: String, callback: (String) -> Unit)
external fun handleGoogleLogin(callback: (String) -> Unit)
external fun signOut()

// Vacancies JS functions declaration
external fun addNewVacancyTrack(companyName: String,
                                companyImageUrl: String,
                                jobName: String,
                                jobDescription: String,
                                jobUrl: String,
                                callback: (String) -> Unit)

class FirebaseAuthManager {

    // Trigger Google login
    fun loginWithGoogle(onSuccess: () -> Unit) {
        handleGoogleLogin { userDataJson ->
            UserSessionManager.saveUserSession(userDataJson)
            onSuccess()
        }
    }

    fun registerWithEmail(
        email: String,
        password: String,
        displayName: String,
        callback: (success: Boolean, errorMessage: String?) -> Unit
    ) {
        createUserWithEmail(email, password, displayName) { response ->
            if (response.contains("\"success\":true")) {
                callback(true, null)
            } else {
                // Parse error message
                val errorStart = response.indexOf("\"message\":\"")
                val errorMessage = if (errorStart >= 0) {
                    val start = errorStart + 11 // length of "message":"
                    val end = response.indexOf("\"", start)
                    if (end >= 0) response.substring(start, end) else "Unknown error"
                } else {
                    "Registration failed"
                }
                callback(false, errorMessage)
            }
        }
        println("Registering with email: $email")
    }

    fun signInWithEmail(
        email: String,
        password: String,
        callback: (success: Boolean, errorMessage: String?) -> Unit
    ) {
        signInWithEmail(email, password) { response ->
            if (response.contains("\"success\":true")) {
                // Success - save the user session
                UserSessionManager.saveUserSession(response)
                callback(true, null)
            } else {
                // Parse error message
                val errorStart = response.indexOf("\"message\":\"")
                val errorMessage = if (errorStart >= 0) {
                    val start = errorStart + 11 // length of "message":"
                    val end = response.indexOf("\"", start)
                    if (end >= 0) response.substring(start, end) else "Unknown error"
                } else {
                    "Registration failed"
                }
                callback(false, errorMessage)
            }
        }
    }

    // Sign out the current user
    fun toSignOut() {
        signOut()
        UserSessionManager.clearUserSession()
    }

    fun toAddNewVacancyTrack(vacancy: JobCartEntity){
        addNewVacancyTrack(
            companyName = vacancy.companyName,
            companyImageUrl = vacancy.companyImageUrl,
            jobName = vacancy.jobName,
            jobDescription = vacancy.jobDescription,
            jobUrl = vacancy.jobUrl
        ){response ->
            println(response)
        }
    }
}