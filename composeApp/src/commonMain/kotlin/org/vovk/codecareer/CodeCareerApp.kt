package org.vovk.codecareer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vovk.codecareer.ui.JobsPage
import org.vovk.codecareer.ui.sidebar.FilterSection
import org.vovk.codecareer.ui.sidebar.ProfileSection

@Composable
fun App(){
    MainScreenWrapper()
}

@Composable
fun MainScreenWrapper() {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val isCompactScreen = screenWidth < 1024.dp
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isCompactScreen) Color.White else Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(1024.dp)
                    .fillMaxHeight()
                    .background(Color.White)
            ) {
                JobSearchScreen()
            }
        }
    }
}

@Composable
fun JobSearchScreen() {
    Row(modifier = Modifier
        .fillMaxWidth(0.8f)
        .background(Color.White),
    ) {
        Column(modifier = Modifier
            .weight(3f)
            .padding(16.dp)
        ) {
            Text(
                text = "Вакансії: к-сть вакансій",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            JobsPage()
        }
        // Sidebar Filters
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
                .background(Color.Gray.copy(alpha = 0.2f))
        ) {
            ProfileSection()
            FilterSection()
        }
    }
}
