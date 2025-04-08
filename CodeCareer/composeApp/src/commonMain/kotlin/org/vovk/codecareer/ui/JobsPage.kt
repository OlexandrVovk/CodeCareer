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

@Composable
fun JobsPage(windowSize: Float = 0.7f) {

    var jobs by remember { mutableStateOf(VacanciesEntityManager.getVacancies()) }

    Column(
        modifier = Modifier.fillMaxWidth(windowSize),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Вакансії: ${jobs.size}",
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
                    JobCard(job)
                }
            }
        }
    }
}


@Composable
fun JobCard(job: JobCartEntity) {
    val isLoggedIn = UserSessionManager.isLoggedIn()
    val firebaseAuth = remember { FirebaseManager() }
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
                        onClick = {firebaseAuth.toAddNewVacancyTrack(job)},
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Track", fontSize = 12.sp)
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