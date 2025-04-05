package org.vovk.codecareer.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.rememberImageSuccessPainter
import com.seiko.imageloader.ui.AutoSizeBox
import org.vovk.codecareer.dal.firebase.FirebaseAuthManager
import org.vovk.codecareer.dal.firebase.UserSessionManager
import org.vovk.codecareer.pages.auth.LoginPage

class AccountPage : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val currentUser = UserSessionManager.currentUser
        println(currentUser)
        val firebaseAuth = remember { FirebaseAuthManager() }
        
        // Redirect if not logged in
        LaunchedEffect(Unit) {
            if (!UserSessionManager.isLoggedIn()) {
                navigator.push(LoginPage())
            }
        }
        
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navigator.popUntilRoot() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                
                Text(
                    text = "My Account",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // User profile section
            if (currentUser != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile picture
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colors.primary, CircleShape)
                    ) {
                        currentUser.photoURL?.let {
                            AutoSizeBox(it) { action ->
                                when (action) {
                                    is ImageAction.Success -> {
                                        Image(
                                            rememberImageSuccessPainter(action),
                                            contentDescription = "image",
                                        )
                                    }
                                    is ImageAction.Loading -> {}
                                    is ImageAction.Failure -> {
                                        // Failure placeholder with initials or icon
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color(0xFFE0E0E0))
                                        ) {
                                            Text(
                                                text = "?",
                                                color = Color.Gray,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.align(Alignment.Center)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    // User name
                    Text(
                        text = currentUser.displayName ?: "User",
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    // User name
                    Text(
                        text = currentUser.email ?: "User",
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Sign out button
                    Button(
                        onClick = {
                            firebaseAuth.toSignOut()
                            navigator.popUntilRoot()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.error,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Sign Out")
                    }
                }
            } else {
                // Loading or error state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}