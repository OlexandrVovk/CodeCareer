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

class RegisterPage : Screen {

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

    private fun isValidName(name: String): Boolean {
        return name.length >= 2
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
        var fullName by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        var confirmPasswordVisible by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }
        var nameError by remember { mutableStateOf("") }
        var emailError by remember { mutableStateOf("") }
        var passwordError by remember { mutableStateOf("") }
        var confirmPasswordError by remember { mutableStateOf("") }
        var registerError by remember { mutableStateOf("") }

        // Track if fields have been touched
        var nameTouched by remember { mutableStateOf(false) }
        var emailTouched by remember { mutableStateOf(false) }
        var passwordTouched by remember { mutableStateOf(false) }
        var confirmPasswordTouched by remember { mutableStateOf(false) }

        // Validate name when touched
        LaunchedEffect(fullName, nameTouched) {
            if (nameTouched) {
                nameError = when {
                    fullName.isEmpty() -> "Name is required"
                    !isValidName(fullName) -> "Name must be at least 2 characters"
                    else -> ""
                }
            }
        }

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

        // Validate confirm password when touched
        LaunchedEffect(confirmPassword, confirmPasswordTouched, password) {
            if (confirmPasswordTouched) {
                confirmPasswordError = when {
                    confirmPassword.isEmpty() -> "Please confirm your password"
                    confirmPassword != password -> "Passwords do not match"
                    else -> ""
                }
            }
        }

        // Password strength
        val passwordStrength = getPasswordStrength(password)

        // Clear auth error when fields change
        LaunchedEffect(email, password, confirmPassword, fullName) {
            if (registerError.isNotEmpty()) {
                registerError = ""
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
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )

                Text(
                    text = "Sign up to get started",
                    style = MaterialTheme.typography.subtitle1,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Error message for registration
                if (registerError.isNotEmpty()) {
                    Text(
                        text = registerError,
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Full Name Field
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    placeholder = { Text("Enter your full name") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Name Icon",
                            tint = if (nameError.isEmpty() || !nameTouched) MaterialTheme.colors.primary else Color.Red
                        )
                    },
                    trailingIcon = {
                        if (nameError.isNotEmpty() && nameTouched) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = Color.Red
                            )
                        } else if (fullName.isNotEmpty() && isValidName(fullName)) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Valid Name",
                                tint = Color(0xFF4CAF50) // Green
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (nameError.isEmpty() || !nameTouched) 16.dp else 4.dp)
                        .onFocusChanged {
                            if (it.isFocused) nameTouched = true
                        },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    isError = nameError.isNotEmpty() && nameTouched
                )

                // Name error message
                if (nameError.isNotEmpty() && nameTouched) {
                    Text(
                        text = nameError,
                        color = Color.Red,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, bottom = 16.dp),
                        textAlign = TextAlign.Start
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
                    placeholder = { Text("Create a strong password") },
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
                        .padding(bottom = if (passwordError.isEmpty() || !passwordTouched) 16.dp else 4.dp)
                        .onFocusChanged {
                            if (it.isFocused) passwordTouched = true
                        },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
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
                Spacer(modifier = Modifier.width(8.dp))
                // Password requirements list
                if (password.isNotEmpty() && passwordTouched) {
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


                // Confirm Password Field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    placeholder = { Text("Confirm your password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Confirm Password Icon",
                            tint = if (confirmPasswordError.isEmpty() || !confirmPasswordTouched) MaterialTheme.colors.primary else Color.Red
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Done else Icons.Default.Warning,
                                contentDescription = if (confirmPasswordVisible) "Hide Password" else "Show Password"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (confirmPasswordError.isEmpty() || !confirmPasswordTouched) 16.dp else 4.dp)
                        .onFocusChanged {
                            if (it.isFocused) confirmPasswordTouched = true
                        },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    isError = confirmPasswordError.isNotEmpty() && confirmPasswordTouched
                )

                // Confirm Password error message
                if (confirmPasswordError.isNotEmpty() && confirmPasswordTouched) {
                    Text(
                        text = confirmPasswordError,
                        color = Color.Red,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, bottom = 16.dp),
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Register Button
                Button(
                    onClick = {
                        if (
                            isValidName(fullName) &&
                            isValidEmail(email) &&
                            isValidPassword(password) &&
                            password == confirmPassword
                        ) {
                            isLoading = true
                            registerError = ""

                            // Call Firebase register function
                            firebaseAuth.registerWithEmail(email, password, fullName) { success, errorMessage ->
                                isLoading = false
                                if (success) {
                                    // Registration successful, navigate to sign in screen
                                    navigator.push(LoginPage())
                                } else {
                                    // Show error message
                                    registerError = errorMessage ?: "Registration failed. Please try again."
                                }
                            }
                        } else {
                            // Validate all fields and show errors
                            nameTouched = true
                            emailTouched = true
                            passwordTouched = true
                            confirmPasswordTouched = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading &&
                            fullName.isNotEmpty() &&
                            email.isNotEmpty() &&
                            password.isNotEmpty() &&
                            confirmPassword.isNotEmpty() &&
                            nameError.isEmpty() &&
                            emailError.isEmpty() &&
                            passwordError.isEmpty() &&
                            confirmPasswordError.isEmpty()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Create Account",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Login Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 32.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account? ",
                        color = Color.Gray
                    )
                    Text(
                        text = "Sign in",
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { navigator.push(LoginPage()) }
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