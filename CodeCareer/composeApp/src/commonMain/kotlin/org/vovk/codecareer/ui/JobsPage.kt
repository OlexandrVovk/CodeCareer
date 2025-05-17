package org.vovk.codecareer.ui

import CircleShapeIcon
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vovk.codecareer.dal.entities.JobCartEntity
import org.vovk.codecareer.dal.firebase.FirebaseManager
import org.vovk.codecareer.dal.firebase.UserSessionManager
import org.vovk.codecareer.dal.vacancies.VacanciesEntityManager
import org.vovk.codecareer.dal.entities.TrackedVacancy
import androidx.compose.material.ButtonDefaults

@Composable
fun JobsPage(windowSize: Float = 0.7f) {
    var jobs by remember { mutableStateOf(VacanciesEntityManager.getVacancies()) }
    // Load tracked vacancies for the logged-in user
    val isLoggedIn = UserSessionManager.isLoggedIn()
    val firebaseManager = remember { FirebaseManager() }
    var trackedVacancies by remember { mutableStateOf<List<TrackedVacancy>>(emptyList()) }
    DisposableEffect(isLoggedIn) {
        if (isLoggedIn) {
            firebaseManager.toGetTrackedVacancies { list ->
                trackedVacancies = list
            }
        }
        onDispose { }
    }
    val trackedUrls = trackedVacancies.map { it.jobInfo.jobUrl }.toSet()

    Column(
        modifier = Modifier.fillMaxWidth(windowSize),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Vacancies: ${jobs.size}",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
            color = Color(199,194, 200)
        )

        if (jobs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Use filters to search vacancies",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn {
                items(jobs) { job ->
                    val isTracked = job.jobUrl in trackedUrls
                    JobCard(
                        job = job,
                        isTracked = isTracked,
                        onToggleTrack = { vacancy ->
                            if (isTracked) {
                                // Untrack vacancy
                                val trackedVacancy = trackedVacancies.firstOrNull { it.jobInfo.jobUrl == vacancy.jobUrl }
                                trackedVacancy?.let { firebaseManager.toDeleteTrackedVacancy(it) }
                            } else {
                                // Track vacancy
                                firebaseManager.toAddNewVacancyTrack(vacancy)
                            }
                            // Refresh tracked vacancies list
                            firebaseManager.toGetTrackedVacancies { list ->
                                trackedVacancies = list
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun JobCard(
    job: JobCartEntity,
    isTracked: Boolean,
    onToggleTrack: (JobCartEntity) -> Unit
) {
    val isLoggedIn = UserSessionManager.isLoggedIn()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
            backgroundColor = Color(17,18,20,255),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircleShapeIcon(job.companyImageUrl)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(job.companyName,
                        fontWeight = FontWeight.Bold,
                        color = Color(199,194,200)
                    )
                }
                if (isLoggedIn) {
                    Button(
                        onClick = { onToggleTrack(job) },
                        modifier = Modifier.height(32.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (isTracked) Color(0xFF4CAF50) else Color(17,18,20,255),
                            contentColor = if (isTracked) Color.White else Color(199,194,200)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(if (isTracked) "Tracked" else "Track", fontSize = 12.sp)
                    }
                }
            }

            Text(job.jobName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { kotlinx.browser.window.open(job.jobUrl, "_blank")},
                color = Color.White
            )
            Text(
                text = job.jobDescription,
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = Color(199,194,200)
            )
        }
    }
}