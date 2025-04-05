package org.vovk.codecareer.dal.firebase

//
//
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.Json
//import org.w3c.dom.events.Event
//
//@Serializable
//data class UserData(
//    val uid: String,
//    val displayName: String? = null,
//    val email: String? = null,
//    val photoURL: String? = null,
//    val signedOut: Boolean = false,
//    val success: Boolean = true,
//    val error: Boolean = false,
//    val code: String? = null,
//    val message: String? = null
//)
//
//// External JS functions declaration
//external fun loginWithGoogle()
//external fun signOut()
//
//class FirebaseManager {
//    // Initialize JSON parser
//    private val json = Json { ignoreUnknownKeys = true }
//
//    // Callback references to prevent garbage collection
//    private var authStateListener: ((Event) -> Unit)? = null
//    private var loginResultListener: ((Event) -> Unit)? = null
//
//    // Setup auth state change listener
//    fun onAuthStateChanged(callback: (UserData) -> Unit) {
//        // Remove previous listener if exists
//        authStateListener?.let { listener ->
//            js("window.removeEventListener('authStateChanged', listener)")
//        }
//
//        // Create new listener
//        authStateListener = { event -> {}}
//
//        // Add the listener
//        js("window.addEventListener('authStateChanged', authStateListener)")
//    }
//
//    // Trigger Google login
//    fun toLoginWithGoogle(callback: (UserData) -> Unit) {
//        // Remove previous listener if exists
//        loginResultListener?.let { listener ->
//            js("window.removeEventListener('googleLoginResult', listener)")
//        }
//        // Create new listener
//        loginResultListener = { event -> {} }
//
//        // Add the listener and start login process
//        js("window.addEventListener('googleLoginResult', loginResultListener)")
//        loginWithGoogle()
//    }
//
//    // Sign out the current user
//    fun toSignOut() {
//        signOut()
//    }
//
//    // Clean up listeners when no longer needed
//    fun cleanup() {
//        authStateListener?.let { listener ->
//            js("window.removeEventListener('authStateChanged', listener)")
//        }
//
//        loginResultListener?.let { listener ->
//            js("window.removeEventListener('googleLoginResult', listener)")
//        }
//    }
//}