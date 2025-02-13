package org.vovk.codecareer.ui

import CircleShapeIcon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun JobsPage(windowSize: Float = 0.7f) {
    Column(
        modifier = Modifier.fillMaxWidth(windowSize),
    ) {
        Text(
            text = "Вакансії: к-сть вакансій",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn {
            items(5) { index ->
                JobCard()
            }
        }
    }
}

@Composable
fun JobCard() {
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
                Text("ФІРМА", fontWeight = FontWeight.Bold)
            }
            Text("НАЗВА ВАКАНСІЇ", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            Text("теги теги теги", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
            Text("опис опис опис опис опис опис опис опис опис опис", fontSize = 14.sp)
        }
    }
}