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
    // Avatar and menu sizing
    val iconSize        = 36.dp
    val menuWidth       = 100.dp

    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color(15, 15, 17, 255),
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "CodeCareer",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(32.dp))
                TextButton(
                    onClick = onNavigateToJobs,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (isJobsActive) Color(199, 194, 200) else LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                    )
                ) {
                    Text(
                        "Jobs",
                        fontWeight = if (isJobsActive) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 16.sp
                    )
                }
                if (isLoggedIn) {
                    Spacer(modifier = Modifier.width(32.dp))
                    TextButton(
                        onClick = onNavigateToTracks,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (isJobsActive) Color(199, 194, 200) else LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                        )
                    ) {
                        Text(
                            "Tracks",
                            fontWeight = if (isJobsActive) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 16.sp
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
                    // Login/Register buttons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateToLogin) {
                            Text("Login", color = Color(199, 194, 200))
                        }
                        IconButton(onClick = onNavigateToRegister) {
                            Text("Register", color = Color(199, 194, 200))
                        }
                    }
                }
            }
        }
    }
}
