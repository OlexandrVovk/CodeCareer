package org.vovk.codecareer.pages.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.vovk.codecareer.dal.firebase.FirebaseManager

class LoginPage : Screen {

    // Validation helper functions
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return email.isNotEmpty() && emailRegex.matches(email)
    }

    // Simplified: only require non-empty password
    private fun isValidPassword(password: String): Boolean {
        return password.isNotEmpty()
    }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val firebaseAuth = remember { FirebaseManager() }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }
        var emailError by remember { mutableStateOf("") }
        var passwordError by remember { mutableStateOf("") }
        var authError by remember { mutableStateOf("") } // For invalid credentials

        // Track if fields have been touched
        var emailTouched by remember { mutableStateOf(false) }
        var passwordTouched by remember { mutableStateOf(false) }

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

        // Validate password when touched
        LaunchedEffect(password, passwordTouched) {
            if (passwordTouched) {
                passwordError = when {
                    password.isEmpty() -> "Password is required"
                    !isValidPassword(password) -> "Password must be at least 8 characters with lowercase, uppercase, number, and special character"
                    else -> ""
                }
            }
        }

        // Clear auth error when fields change
        LaunchedEffect(email, password) {
            if (authError.isNotEmpty()) {
                authError = ""
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(15,15,17,255)),
            contentAlignment = Alignment.Center
        ) {
            val isMobile = maxWidth < 425.dp
            val scrollState = rememberScrollState()
            val columnModifier = Modifier
                .fillMaxWidth(if (isMobile) 1f else 0.5f)
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp)
                .let { if (isMobile) it.verticalScroll(scrollState) else it }

            Column(
                modifier = columnModifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )

                Text(
                    text = "Sign in to continue",
                    style = MaterialTheme.typography.subtitle1,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 40.dp)
                )

                // Error message for authentication
                if (authError.isNotEmpty()) {
                    Text(
                        text = authError,
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", color = Color.Gray) },
                    placeholder = { Text("Enter your email", color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email Icon",
                            tint = if (emailError.isEmpty() || !emailTouched) Color.Gray else Color.Red
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.Gray,
                        focusedBorderColor = Color(57, 60, 64, 255),
                        unfocusedBorderColor = Color(57, 60, 64, 255)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (emailError.isEmpty() || !emailTouched) 16.dp else 4.dp)
                        .onFocusChanged {
                            if (it.isFocused) emailTouched = true
                        },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    isError = emailError.isNotEmpty() && emailTouched
                )

                // Email error message
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

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password" , color = Color.Gray) },
                    placeholder = { Text("Enter your password" , color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password Icon",
                            tint = if (passwordError.isEmpty() || !passwordTouched) Color.Gray else Color.Red
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.Gray,
                        focusedBorderColor = Color(57, 60, 64, 255),
                        unfocusedBorderColor = Color(57, 60, 64, 255)
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = if (passwordError.isEmpty() || !passwordTouched) Color.Gray else Color.Red
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (passwordError.isEmpty() || !passwordTouched) 8.dp else 4.dp)
                        .onFocusChanged {
                            if (it.isFocused) passwordTouched = true
                        },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    isError = passwordError.isNotEmpty() && passwordTouched
                )

                // Password error message
                if (passwordError.isNotEmpty() && passwordTouched) {
                    Text(
                        text = passwordError,
                        color = Color.Red,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, bottom = 8.dp),
                        textAlign = TextAlign.Start
                    )
                }

                // Forgot Password Text
                Text(
                    text = "Forgot Password?",
                    color = Color(30, 144, 255, 255),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 24.dp)
                        .clickable { navigator.push(ResetPasswordPage()) },
                    fontWeight = FontWeight.Medium
                )

                // Login Button
                Button(
                    onClick = {
                        isLoading = true
                        // Simulate auth check - in real app, call your auth service
                        if (isValidEmail(email) && isValidPassword(password)) {
                            firebaseAuth.signInWithEmail(
                                email = email,
                                password = password
                            ) { success, errorMessage ->
                                isLoading = false
                                if (success) {
                                    navigator.popUntilRoot()
                                } else {
                                    authError = errorMessage ?: "Login failed"
                                }
                            }
                        } else {
                            // Invalid credentials
                            authError = "Invalid email or password. Please try again."
                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty() && emailError.isEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        disabledBackgroundColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.DarkGray,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Sign In",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = Color.LightGray
                    )
                    Text(
                        text = " OR ",
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = Color.LightGray
                    )
                }

                // Google Login Button
                OutlinedButton(
                    onClick = {
                        firebaseAuth.loginWithGoogle(onSuccess = {
                            isLoading = false
                            navigator.popUntilRoot()
                        })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        disabledBackgroundColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                }

                // Register Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account? ",
                        color = Color.Gray
                    )
                    Text(
                        text = "Register",
                        color = Color(30, 144, 255, 255),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { navigator.push(RegisterPage())  }
                    )
                }
            }
        }
    }

    @Composable
    private fun PasswordRequirement(
        text: String,
        satisfied: Boolean
    ) {
        Row(
            modifier = Modifier.padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (satisfied) Icons.Default.Done else Icons.Default.Warning,
                contentDescription = if (satisfied) "Requirement met" else "Requirement not met",
                tint = if (satisfied) Color(0xFF4CAF50) else Color.Gray,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.caption,
                color = if (satisfied) Color(0xFF4CAF50) else Color.Gray
            )
        }
    }
}