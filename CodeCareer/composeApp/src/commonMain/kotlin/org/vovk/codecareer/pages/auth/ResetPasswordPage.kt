package org.vovk.codecareer.pages.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.vovk.codecareer.dal.firebase.FirebaseManager

/**
 * Screen for resetting user password via email.
 */
class ResetPasswordPage : Screen {

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
        return email.isNotEmpty() && emailRegex.matches(email)
    }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val firebaseAuth = remember { FirebaseManager() }
        var email by remember { mutableStateOf("") }
        var emailTouched by remember { mutableStateOf(false) }
        var emailError by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var resetMessage by remember { mutableStateOf<String?>(null) }
        var resetError by remember { mutableStateOf(false) }

        // Validate email when touched
        LaunchedEffect(email, emailTouched) {
            if (emailTouched) {
                emailError = when {
                    email.isEmpty() -> "Email is required"
                    !isValidEmail(email) -> "Please enter a valid email address"
                    else -> ""
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(15, 15, 17)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(horizontal = 24.dp)
                    .padding(top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Reset Password",
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Enter your email to reset your password",
                    style = MaterialTheme.typography.subtitle1,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Show reset status message
                resetMessage?.let { msg ->
                    Text(
                        text = msg,
                        color = if (resetError) Color.Red else Color(0xFF4CAF50),
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", color = Color.Gray) },
                    placeholder = { Text("Enter your email", color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = if (emailError.isEmpty() || !emailTouched) Color.Gray else Color.Red
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (emailError.isEmpty() || !emailTouched) 16.dp else 4.dp)
                        .onFocusChanged { if (it.isFocused) emailTouched = true },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.Gray,
                        focusedBorderColor = Color(57, 60, 64, 255),
                        unfocusedBorderColor = Color(57, 60, 64, 255)
                    ),
                    singleLine = true,
                    isError = emailError.isNotEmpty() && emailTouched
                )
                if (emailError.isNotEmpty() && emailTouched) {
                    Text(
                        text = emailError,
                        color = Color.Red,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, bottom = 16.dp),
                        textAlign = TextAlign.Start
                    )
                }

                Button(
                    onClick = {
                        isLoading = true
                        if (isValidEmail(email)) {
                            firebaseAuth.resetPassword(email) { success, errMsg ->
                                isLoading = false
                                resetError = !success
                                resetMessage = if (success) {
                                    "Check your inbox for reset instructions."
                                } else {
                                    errMsg ?: "Failed to send reset email."
                                }
                            }
                        } else {
                            isLoading = false
                            resetError = true
                            resetMessage = "Please enter a valid email address"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading && email.isNotEmpty() && emailError.isEmpty()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Reset Password",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Remembered your password? ",
                        color = Color.Gray
                    )
                    Text(
                        text = "Sign In",
                        color = Color(30, 144, 255),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { navigator.pop() }
                    )
                }
            }
        }
    }
}