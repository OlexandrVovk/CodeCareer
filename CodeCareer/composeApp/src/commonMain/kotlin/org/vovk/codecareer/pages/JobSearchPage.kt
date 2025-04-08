package org.vovk.codecareer.pages

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
import cafe.adriel.voyager.core.screen.Screen
import org.vovk.codecareer.ui.JobsPage
import org.vovk.codecareer.ui.sidebar.FilterSection

class JobSearchPage: Screen {
    @Composable
    override fun Content() {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(15,15,17,255))
        ) {
            val (isFilterOverlayVisible, setFilterOverlayVisible) = remember { mutableStateOf(false) }
            val screenWidth = maxWidth

            if (screenWidth > 1023.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(15,15,17,255)),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    JobsPage(0.65f)
                    FilterSection()
                }
            }
            else {
                // Display main content with a "Filters" button at the bottom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(15,15,17,255))
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
                        .background(Color(15,15,17,255))
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
}