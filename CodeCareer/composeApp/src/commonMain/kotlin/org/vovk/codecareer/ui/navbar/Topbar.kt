package org.vovk.codecareer.ui.navbar

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    isJobsActive: Boolean = true
) {
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
            }

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