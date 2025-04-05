package org.vovk.codecareer.ui.navbar

import androidx.compose.foundation.layout.*
import androidx.compose.material.* // Use material or material3 based on your project setup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle // Example profile icon
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
 * @param onNavigateToProfile Callback invoked when the "Profile" action is clicked.
 * @param isJobsActive Indicates if the Jobs page is the current active page to potentially highlight it.
 */
@Composable
fun CodeCareerTopAppBar(
    modifier: Modifier = Modifier,
    onNavigateToJobs: () -> Unit,
    onNavigateToProfile: () -> Unit,
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
                IconButton(onClick = onNavigateToProfile) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle, // Standard profile icon
                        contentDescription = "Profile", // Accessibility
                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.8f) // Adjust tint as needed
                    )
                }
            }
        }
    }
}