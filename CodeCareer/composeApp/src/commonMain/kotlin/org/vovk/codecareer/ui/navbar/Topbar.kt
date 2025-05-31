package org.vovk.codecareer.ui.navbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.DpOffset
import cafe.adriel.voyager.navigator.Navigator
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.rememberImageSuccessPainter
import com.seiko.imageloader.ui.AutoSizeBox
import org.vovk.codecareer.dal.firebase.UserSessionManager
import org.vovk.codecareer.dal.firebase.FirebaseManager
import androidx.compose.foundation.BorderStroke

/**
 * A Composable function for the top application bar of CodeCareer.
 *
 * @param modifier Optional Modifier for the TopAppBar.
 * @param onNavigateToJobs Callback invoked when the "Jobs" item is clicked.
 * @param onNavigateToLogin Callback invoked when the "Login" action is clicked.
 * @param onNavigateToRegister Callback invoked when the "Register" action is clicked.
 * @param isJobsActive Indicates if the Jobs page is the current active page to potentially highlight it.
 */
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    onNavigateToJobs: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToTracks: () -> Unit,
    isJobsActive: Boolean = true,
    navigator: Navigator
) {
    val isLoggedIn      = UserSessionManager.isLoggedIn()
    val currentUser     = UserSessionManager.currentUser
    val firebaseAuth    = remember { FirebaseManager() }
    val menuExpanded    = remember { mutableStateOf(false) }
    val menuWidth       = 100.dp

    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color(15, 15, 17, 255),
        elevation = 0.dp
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val maxWidthDp = maxWidth
            val smallScreen = maxWidthDp < 340.dp
            val mediumScreen = maxWidthDp < 400.dp
            // On mobile presets (S, M, L), hide the main title to save space
            val isMobile = maxWidthDp < 425.dp
            val spacerWidth = when {
                smallScreen -> 16.dp
                mediumScreen -> 24.dp
                else -> 32.dp
            }
            val titleFontSize = when {
                smallScreen -> 16.sp
                mediumScreen -> 18.sp
                else -> 20.sp
            }
            val tabFontSize = when {
                smallScreen -> 14.sp
                mediumScreen -> 15.sp
                else -> 16.sp
            }
            val iconSize = when {
                smallScreen -> 28.dp
                mediumScreen -> 32.dp
                else -> 36.dp
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isMobile) {
                    Text(
                        text = "CodeCareer",
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(spacerWidth))
                }
                TextButton(
                    onClick = onNavigateToJobs,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (isJobsActive) Color(199, 194, 200) else LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                    )
                ) {
                    Text(
                        "Jobs",
                        fontWeight = if (isJobsActive) FontWeight.Bold else FontWeight.Normal,
                        fontSize = tabFontSize
                    )
                }
                if (isLoggedIn) {
                    Spacer(modifier = Modifier.width(spacerWidth))
                    TextButton(
                        onClick = onNavigateToTracks,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (isJobsActive) Color(199, 194, 200) else LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                        )
                    ) {
                        Text(
                            "Tracks",
                            fontWeight = if (isJobsActive) FontWeight.Bold else FontWeight.Normal,
                            fontSize = tabFontSize
                        )
                    }
                }
            }
            // Profile avatar + logout menu
            Box {
                if (isLoggedIn && currentUser != null) {
                    // Avatar icon (placeholder or photo)
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(iconSize)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0))
                            .clickable { menuExpanded.value = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentUser.photoURL == null) {
                            Text(
                                text = "?",
                                color = Color(199, 194, 200),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            AutoSizeBox(
                                currentUser.photoURL,
                                modifier = Modifier.fillMaxSize()
                            ) { action ->
                                when (action) {
                                    is ImageAction.Success -> {
                                        Image(
                                            rememberImageSuccessPainter(action),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    else -> { /* loading/failure placeholder via background */ }
                                }
                            }
                        }
                    }
                    // Logout dropdown menu, aligned under avatar's right edge
                    DropdownMenu(
                        expanded = menuExpanded.value,
                        onDismissRequest = { menuExpanded.value = false },
                        offset = DpOffset(iconSize - menuWidth, 0.dp),
                        modifier = Modifier
                            .width(menuWidth)
                            .background(Color(17, 18, 20, 255))
                            .border(2.dp, Color(57, 60, 64, 255))
                    ) {
                        DropdownMenuItem(onClick = {
                            firebaseAuth.toSignOut()
                            navigator.popUntilRoot()
                            menuExpanded.value = false
                        }) {
                            Text("Log out", color = Color.Red)
                        }
                    }
                } else {
                    // Login/Register buttons with gray border and spacing
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = onNavigateToLogin,
                            border = BorderStroke(2.dp, Color(57,60,64,255)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = Color.Transparent,
                            ),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Login", fontSize = 16.sp, color = Color(199, 194, 200))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = onNavigateToRegister,
                            border = BorderStroke(2.dp, Color(57,60,64,255)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = Color.Transparent,
                            ),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Register", fontSize = 16.sp, color = Color(199, 194, 200))
                        }
                    }
                }
            }
        }
    }
    }
}
