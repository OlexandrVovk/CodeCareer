package org.vovk.codecareer.dal.firebase

import kotlinx.browser.window
import org.w3c.dom.events.Event

// External JS functions declaration
external fun handleGoogleLogin(callback: (String) -> Unit)
external fun signOut()

class FirebaseAuthManager {
    // Callback references to prevent garbage collection
    private var authStateListener: ((Event) -> Unit)? = null

//    init {
//        // Setup auth state change listener from JS
//        setupAuthStateListener()
//    }

//    private fun setupAuthStateListener() {
//        // Create new listener
//        authStateListener = { event ->
//            // Check if event has detail property with user data
//            val customEvent = event
//            val userDataJson = customEvent .detail as? String
//
//            if (userDataJson != null) {
//                // Process auth state change (can be login or logout)
//                processAuthStateChange(userDataJson)
//            }
//        }
//
//        // Add the listener
//        window.addEventListener("authStateChanged", authStateListener)
//    }

    private fun processAuthStateChange(userDataJson: String) {
        if (userDataJson.contains("\"signedOut\":true")) {
            // Handle sign out event
            UserSessionManager.clearUserSession()
        } else {
            // Handle sign in event
            UserSessionManager.saveUserSession(userDataJson)
        }
    }

    // Trigger Google login
    fun loginWithGoogle(onSuccess: () -> Unit) {
        handleGoogleLogin { userDataJson ->
            UserSessionManager.saveUserSession(userDataJson)
            onSuccess()
        }
    }

    // Sign out the current user
    fun toSignOut() {
        signOut()
        UserSessionManager.clearUserSession()
    }

    // Clean up listeners when no longer needed
    fun cleanup() {
        authStateListener?.let { listener ->
            window.removeEventListener("authStateChanged", listener)
        }
    }
}