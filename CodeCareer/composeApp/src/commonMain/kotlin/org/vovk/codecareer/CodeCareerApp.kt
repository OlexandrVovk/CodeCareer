package org.vovk.codecareer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vovk.codecareer.ui.JobsPage
import org.vovk.codecareer.ui.sidebar.FilterSection

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
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val (isFilterOverlayVisible, setFilterOverlayVisible) = remember { mutableStateOf(false) }
        val screenWidth = maxWidth

        if (screenWidth >= 1024.dp) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                JobsPage(0.7f)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.Gray.copy(alpha = 0.2f))
                ) {
                    FilterSection()
                }
            }
        }
        else {
            // Display main content with a "Filters" button at the bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                JobsPage(1f)
                // "Filters" button at the bottom
                Button(
                    onClick = { setFilterOverlayVisible(!isFilterOverlayVisible) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Filters",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        if (isFilterOverlayVisible) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                FilterSection()
                // "Filters" button at the bottom
                Button(
                    onClick = { setFilterOverlayVisible(!isFilterOverlayVisible) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Filters",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

