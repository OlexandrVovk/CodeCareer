package org.vovk.codecareer.ui.calendar

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import org.vovk.codecareer.dal.enums.InterviewType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.vovk.codecareer.dal.entities.TrackedVacancy

external fun getTodaysDate(): String

@Composable
fun CalendarDialog(
    vacancy: TrackedVacancy,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val todaysDate = getTodaysDate()
    var selectedDate by remember { mutableStateOf(todaysDate) }
    var selectedInterviewType by remember { mutableStateOf<InterviewType?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var eventHours by remember { mutableStateOf(9) } // Default to 9:00 AM
    var eventMinutes by remember { mutableStateOf(0) }
    var eventNotes by remember { mutableStateOf("") }

    // Format time as HH:MM for display and saving
    val formattedTime = remember(eventHours, eventMinutes) {
        val hours = eventHours.toString().padStart(2, '0')
        val minutes = eventMinutes.toString().padStart(2, '0')
        "$hours:$minutes"
    }

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

                // Calendar view
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 360.dp)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    // Parse the selected date or use default
                    val dateParts = selectedDate.split("-").map { it.toInt() }
                    val initialDate = if (dateParts.size == 3) {
                        CalendarDate(dateParts[0], dateParts[1], dateParts[2])
                    } else {
                        CalendarDate.now()
                    }

                    // Use our custom calendar component
                    CustomCalendar(
                        initialDate = initialDate,
                        onDateSelected = { date ->
                            selectedDate = date.format()
                        },
                        accentColor = Color(0xFF864AED),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Event details
                // Dropdown for interview types
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = { isDropdownExpanded = true }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedInterviewType?.displayName ?: "Select interview type",
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown"
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                    ) {
                        InterviewType.entries.forEach { interviewType ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedInterviewType = interviewType
                                    isDropdownExpanded = false
                                }
                            ) {
                                Text(interviewType.displayName)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Time selector component
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Select time", modifier = Modifier.padding(bottom = 4.dp))
                    TimeSelector(
                        hours = eventHours,
                        minutes = eventMinutes,
                        onHoursChange = { eventHours = it },
                        onMinutesChange = { eventMinutes = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

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
                    "Selected date: $selectedDate at $formattedTime",
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
