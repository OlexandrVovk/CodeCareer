package org.vovk.codecareer.ui.navbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.rememberImageSuccessPainter
import com.seiko.imageloader.ui.AutoSizeBox
import org.vovk.codecareer.dal.firebase.UserSessionManager
import org.vovk.codecareer.pages.AccountPage

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
fun CodeCareerTopAppBar(
    modifier: Modifier = Modifier,
    onNavigateToJobs: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToTracks: () -> Unit,
    isJobsActive: Boolean = true,
    navigator: Navigator
) {
    val isLoggedIn = UserSessionManager.isLoggedIn()
    val currentUser = UserSessionManager.currentUser

    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "CodeCareer",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )
                    Spacer(modifier = Modifier.width(32.dp))
                    TextButton(
                        onClick = onNavigateToJobs,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (isJobsActive) MaterialTheme.colors.primary else LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
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
                                contentColor = if (isJobsActive) MaterialTheme.colors.primary else LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
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
            Box{
                if (isLoggedIn && currentUser != null){
                    if (currentUser.photoURL == null){
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE0E0E0))
                                .clickable {
                                    navigator.push(AccountPage())
                                }
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
                    currentUser.photoURL?.let {
                        AutoSizeBox(it,
                            modifier = Modifier.clickable {
                                navigator.push(AccountPage())
                            }.clip(CircleShape)
                        ) { action ->
                            when (action) {
                                is ImageAction.Success -> {
                                    Image(
                                        rememberImageSuccessPainter(action),
                                        contentDescription = "image",
                                    )
                                }
                                is ImageAction.Loading -> {}
                                is ImageAction.Failure -> {
                                    Box(
                                        modifier = Modifier
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
                }else{
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateToLogin) {
                            Text("Login")
                        }
                        IconButton(onClick = onNavigateToRegister) {
                            Text("Register")
                        }
                    }
                }
            }

        }

    }
}