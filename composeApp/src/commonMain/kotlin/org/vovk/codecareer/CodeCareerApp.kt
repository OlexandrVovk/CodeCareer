package org.vovk.codecareer

import CircleShapeIcon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
    JobSearchScreen()
}

@Composable
fun JobSearchScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
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

}
