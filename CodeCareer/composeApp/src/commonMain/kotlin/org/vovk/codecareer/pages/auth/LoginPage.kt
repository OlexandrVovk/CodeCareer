package org.vovk.codecareer.pages.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import org.vovk.codecareer.dal.firebase.FirebaseAuthManager

external fun handleGoogleLogin(callback: (String) -> Unit)


class LoginPage : Screen {

    // Validation helper functions
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return email.isNotEmpty() && emailRegex.matches(email)
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isLowerCase() } &&
                password.any { it.isUpperCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }
    }

    private fun getPasswordStrength(password: String): PasswordStrength {
        if (password.isEmpty()) return PasswordStrength.EMPTY

        var strength = 0
        if (password.length >= 8) strength++
        if (password.any { it.isLowerCase() }) strength++
        if (password.any { it.isUpperCase() }) strength++
        if (password.any { it.isDigit() }) strength++
        if (password.any { !it.isLetterOrDigit() }) strength++

        return when(strength) {
            1 -> PasswordStrength.WEAK
            2 -> PasswordStrength.WEAK
            3 -> PasswordStrength.MEDIUM
            4 -> PasswordStrength.STRONG
            5 -> PasswordStrength.VERY_STRONG
            else -> PasswordStrength.EMPTY
        }
    }

    enum class PasswordStrength(val color: Color) {
        EMPTY(Color.Gray),
        WEAK(Color.Red),
        MEDIUM(Color(0xFFFFA500)), // Orange
        STRONG(Color(0xFF4CAF50)), // Green
        VERY_STRONG(Color(0xFF008000)) // Dark Green
    }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val firebaseAuth = remember { FirebaseAuthManager() }
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

        // Password strength
        val passwordStrength = getPasswordStrength(password)

        // Clear auth error when fields change
        LaunchedEffect(email, password) {
            if (authError.isNotEmpty()) {
                authError = ""
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
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
                    text = "Welcome Back",
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
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
                    label = { Text("Email") },
                    placeholder = { Text("Enter your email") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email Icon",
                            tint = if (emailError.isEmpty() || !emailTouched) MaterialTheme.colors.primary else Color.Red
                        )
                    },
                    trailingIcon = {
                        if (emailError.isNotEmpty() && emailTouched) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = Color.Red
                            )
                        } else if (email.isNotEmpty() && isValidEmail(email)) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Valid Email",
                                tint = Color(0xFF4CAF50) // Green
                            )
                        }
                    },
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
                    label = { Text("Password") },
                    placeholder = { Text("Enter your password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password Icon",
                            tint = if (passwordError.isEmpty() || !passwordTouched) MaterialTheme.colors.primary else Color.Red
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Done else Icons.Default.Warning,
                                contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
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

                // Password strength indicator
                if (password.isNotEmpty() && passwordTouched) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Password strength: ",
                            style = MaterialTheme.typography.caption,
                            color = Color.Gray
                        )
                        Text(
                            text = when(passwordStrength) {
                                PasswordStrength.EMPTY -> ""
                                PasswordStrength.WEAK -> "Weak"
                                PasswordStrength.MEDIUM -> "Medium"
                                PasswordStrength.STRONG -> "Strong"
                                PasswordStrength.VERY_STRONG -> "Very Strong"
                            },
                            style = MaterialTheme.typography.caption,
                            color = passwordStrength.color,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        LinearProgressIndicator(
                            progress = when(passwordStrength) {
                                PasswordStrength.EMPTY -> 0f
                                PasswordStrength.WEAK -> 0.2f
                                PasswordStrength.MEDIUM -> 0.5f
                                PasswordStrength.STRONG -> 0.8f
                                PasswordStrength.VERY_STRONG -> 1f
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp),
                            color = passwordStrength.color,
                            backgroundColor = Color.LightGray
                        )
                    }

                    // Password requirements list
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Password must have:",
                            style = MaterialTheme.typography.caption,
                            color = Color.Gray
                        )

                        PasswordRequirement(
                            text = "At least 8 characters",
                            satisfied = password.length >= 8
                        )

                        PasswordRequirement(
                            text = "At least 1 lowercase letter (a-z)",
                            satisfied = password.any { it.isLowerCase() }
                        )

                        PasswordRequirement(
                            text = "At least 1 uppercase letter (A-Z)",
                            satisfied = password.any { it.isUpperCase() }
                        )

                        PasswordRequirement(
                            text = "At least 1 number (0-9)",
                            satisfied = password.any { it.isDigit() }
                        )

                        PasswordRequirement(
                            text = "At least 1 special character (!@#\$%^&*...)",
                            satisfied = password.any { !it.isLetterOrDigit() }
                        )
                    }
                }

                // Forgot Password Text
                Text(
                    text = "Forgot Password?",
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 24.dp)
                        .clickable { /* Handle password reset */ },
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
                    enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty() &&
                            emailError.isEmpty() && passwordError.isEmpty()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Sign In",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
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
//                        handleGoogleLogin { messageFromJs ->
//                            println("Kotlin: Received message from JS event: '$messageFromJs'")
//                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            fontSize = 16.sp,
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
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { /* Navigate to registration page */ }
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