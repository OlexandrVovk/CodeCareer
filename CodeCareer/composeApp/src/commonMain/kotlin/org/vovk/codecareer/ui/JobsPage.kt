package org.vovk.codecareer.ui

import CircleShapeIcon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vovk.codecareer.dal.entities.JobCartEntity

@Composable
fun JobsPage(windowSize: Float = 0.7f) {
    val jobList = listOf(
        JobCartEntity("Google", "Kotlin Developer", "Developing Android apps using Kotlin", listOf("Kotlin", "Android", "Jetpack"), "$100k"),
        JobCartEntity("Amazon", "Backend Engineer", "Building scalable microservices", listOf("Kotlin", "Spring Boot", "AWS"), "$120k"),
        JobCartEntity("Facebook", "Mobile Developer", "Working on Messenger", listOf("Compose", "Kotlin", "iOS"), "$110k")
    )
    Column(
        modifier = Modifier.fillMaxWidth(windowSize),
    ) {
        Text(
            text = "Вакансії: ${jobList.size}",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn {
            items(jobList) { job ->
                JobCard(job)
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
                CircleShapeIcon()
                Spacer(modifier = Modifier.width(8.dp))
                Text(job.firmName, fontWeight = FontWeight.Bold)
            }
            Text(job.jobName, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            Text(job.tags.joinToString(", "), fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
            Text(job.jobDescription, fontSize = 14.sp)
            Text("Salary: ${job.salary}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Green, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
