package org.vovk.codecareer.ui

import CircleShapeIcon
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
            modifier = Modifier.padding(bottom = 16.dp)
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircleShapeIcon(job.companyUrl)
                Spacer(modifier = Modifier.width(8.dp))
                Text(job.companyName, fontWeight = FontWeight.Bold)
            }
            Text(job.jobName, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            Text(job.tags.joinToString(", "), fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
            Text(
                text = job.jobDescription,
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
