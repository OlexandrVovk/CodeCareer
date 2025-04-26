package org.vovk.codecareer.ui.tracks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.vovk.codecareer.dal.entities.TrackedVacancy

@Composable
fun CalendarDialog(
    vacancy: TrackedVacancy,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf("2023-06-15") } // Default date
    var eventTitle by remember { mutableStateOf("") }
    var eventTime by remember { mutableStateOf("") }
    var eventNotes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Calendar",
                    tint = Color(0xFF864AED),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Schedule for Vacancy")
            }
        },
        text = {
            Column(modifier = Modifier.padding(8.dp)) {
                // Vacancy info
                Text(
                    text = vacancy.jobInfo.jobName,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "at ${vacancy.jobInfo.companyName}",
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Calendar view (mocked)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        // Month and year header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("June 2023", fontWeight = FontWeight.Bold)
                            Row {
                                IconButton(onClick = { /* Previous month */ }) {
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
                                }
                                IconButton(onClick = { /* Next month */ }) {
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Days of week header
                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { day ->
                                Text(
                                    text = day,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Calendar grid (mocked)
                        Column {
                            for (week in 0 until 5) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    for (day in 1..7) {
                                        val date = week * 7 + day
                                        if (date <= 30) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f)
                                                    .padding(2.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(
                                                        if (date == 15) Color(0xFF864AED).copy(alpha = 0.2f)
                                                        else Color.Transparent
                                                    )
                                                    .border(
                                                        width = if (date == 15) 1.dp else 0.dp,
                                                        color = if (date == 15) Color(0xFF864AED) else Color.Transparent,
                                                        shape = RoundedCornerShape(4.dp)
                                                    )
                                                    .clickable {
                                                        selectedDate = "2023-06-$date"
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = date.toString(),
                                                    color = if (date == 15) Color(0xFF864AED) else Color.Unspecified,
                                                    fontWeight = if (date == 15) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Event details
                OutlinedTextField(
                    value = eventTitle,
                    onValueChange = { eventTitle = it },
                    label = { Text("Event Title") },
                    placeholder = { Text("e.g., Interview with HR") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = eventTime,
                    onValueChange = { eventTime = it },
                    label = { Text("Time") },
                    placeholder = { Text("e.g., 14:30") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = eventNotes,
                    onValueChange = { eventNotes = it },
                    label = { Text("Notes") },
                    placeholder = { Text("Any additional details...") },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Selected date: $selectedDate",
                    color = Color(0xFF864AED),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF864AED),
                    contentColor = Color.White
                )
            ) {
                Text("Save Event")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}