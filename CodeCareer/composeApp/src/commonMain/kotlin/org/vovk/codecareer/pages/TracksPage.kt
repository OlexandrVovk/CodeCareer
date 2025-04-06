package org.vovk.codecareer.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.browser.window
import org.vovk.codecareer.dal.entities.TrackedVacancy
import org.vovk.codecareer.dal.entities.VacancyStatus
import org.vovk.codecareer.dal.firebase.FirebaseManager
import org.vovk.codecareer.dal.firebase.UserSessionManager
import org.vovk.codecareer.pages.auth.LoginPage

class TracksPage : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val firebaseManager = remember { FirebaseManager() }

        // State for tracked vacancies
        var trackedVacancies by remember { mutableStateOf<List<TrackedVacancy>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        // Fetch tracked vacancies when the page loads
        LaunchedEffect(Unit) {
            if (!UserSessionManager.isLoggedIn()) {
                navigator.push(LoginPage())
                return@LaunchedEffect
            }

            firebaseManager.toGetTrackedVacancies { vacancies ->
                trackedVacancies = vacancies
                isLoading = false
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "My Tracked Vacancies",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading) {
                // Show loading indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                // Show error message
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            errorMessage ?: "An error occurred",
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = {
                            isLoading = true
                            errorMessage = null
                            firebaseManager.toGetTrackedVacancies { vacancies ->
                                trackedVacancies = vacancies
                                isLoading = false
                            }
                        }) {
                            Text("Try Again")
                        }
                    }
                }
            } else {
                // Table header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEEEEEE))
                        .padding(8.dp)
                ) {
                    Text(
                        "Vacancy",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(2f)
                    )
                    Text(
                        "Status",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "Notes",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(2f)
                    )
                }

                // Table content
                if (trackedVacancies.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "You haven't tracked any vacancies yet.\nGo to the Jobs page to start tracking!",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(trackedVacancies) { vacancy ->
                            VacancyRow(
                                vacancy = vacancy,
                                onStatusChange = { newStatus ->
                                    trackedVacancies = trackedVacancies.map {
                                        if (it.jobInfo.jobUrl == vacancy.jobInfo.jobUrl) {
                                            val updatedTrack = it.copy(status = newStatus)
                                            firebaseManager.toUpdateTrackedVacancy(updatedTrack)
                                            updatedTrack
                                        } else {
                                            it
                                        }
                                    }
                                },
                                onNotesChange = { newNotes ->
                                    trackedVacancies = trackedVacancies.map {
                                        if (it.jobInfo.jobUrl == vacancy.jobInfo.jobUrl) {
                                            val updatedTrack = it.copy(notes = newNotes)
                                            firebaseManager.toUpdateTrackedVacancy(updatedTrack)
                                            updatedTrack
                                        } else {
                                            it
                                        }
                                    }
                                }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun VacancyRow(
        vacancy: TrackedVacancy,
        onStatusChange: (VacancyStatus) -> Unit,
        onNotesChange: (String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        var editingNotes by remember { mutableStateOf(false) }
        var notesText by remember { mutableStateOf(vacancy.notes) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Column 1: Vacancy & Company
            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    vacancy.jobInfo.jobName,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "at ${vacancy.jobInfo.companyName}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    "View Details",
                    color = MaterialTheme.colors.primary,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable {
                            // Open job URL in new tab
                            window.open(vacancy.jobInfo.jobUrl, "_blank")
                        }
                )
            }

            // Column 2: Status dropdown
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = getStatusColor(vacancy.status).copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        vacancy.status.displayName,
                        color = getStatusColor(vacancy.status),
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select Status",
                        tint = getStatusColor(vacancy.status)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.width(200.dp)
                ) {
                    VacancyStatus.entries.forEach { status ->
                        DropdownMenuItem(
                            onClick = {
                                onStatusChange(status)
                                expanded = false
                            }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            getStatusColor(status),
                                            RoundedCornerShape(2.dp)
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(status.displayName)
                            }
                        }
                    }
                }
            }

            // Column 3: Notes
            Box(
                modifier = Modifier
                    .weight(2f)
                    .padding(start = 8.dp)
            ) {
                if (editingNotes) {
                    Column {
                        TextField(
                            value = notesText,
                            onValueChange = { notesText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.White
                            ),
                            placeholder = { Text("Add notes about this application...") }
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    editingNotes = false
                                    notesText = vacancy.notes
                                }
                            ) {
                                Text("Cancel")
                            }
                            TextButton(
                                onClick = {
                                    onNotesChange(notesText)
                                    editingNotes = false
                                }
                            ) {
                                Text("Save")
                            }
                        }
                    }
                } else {
                    Column(modifier = Modifier.clickable { editingNotes = true }) {
                        if (vacancy.notes.isBlank()) {
                            Text(
                                "Click to add notes...",
                                color = Color.Gray,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        } else {
                            Text(
                                vacancy.notes,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (vacancy.notes.length > 100) {
                                Text(
                                    "Click to edit...",
                                    color = MaterialTheme.colors.primary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getStatusColor(status: VacancyStatus): Color {
        return when (status) {
            VacancyStatus.INTERESTED -> Color(0xFF2196F3)      // Blue
            VacancyStatus.RESUME_SENT -> Color(0xFFFF9800)     // Orange
            VacancyStatus.INTERVIEW_SCHEDULED -> Color(0xFF673AB7) // Purple
            VacancyStatus.OFFER -> Color(0xFF4CAF50)           // Green
            VacancyStatus.REJECTED -> Color(0xFFF44336)        // Red
            VacancyStatus.NOT_INTERESTED -> Color(0xFF9E9E9E)  // Gray
        }
    }
}