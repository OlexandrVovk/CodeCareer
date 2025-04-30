package org.vovk.codecareer.ui.calendar

import androidx.compose.foundation.background
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
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vovk.codecareer.dal.entities.InterviewSchedule
import org.vovk.codecareer.dal.entities.TrackedVacancy

external fun getTodaysDate(): String

// Define the steps in the calendar flow
enum class CalendarStep {
    DATE_SELECTION,
    TIME_SELECTION,
    EVENT_SUMMARY
}

@Composable
fun CalendarDialog(
    vacancy: TrackedVacancy,
    onConfirm: (TrackedVacancy) -> Unit,
    onDismiss: () -> Unit
) {
    val todaysDate = getTodaysDate()
    val accentColor = Color(0xFF864AED)
    val errorColor = Color(0xFFE57373)

    // Get existing interview schedule data if available
    val existingSchedule = vacancy.interviewSchedule

    // Track the current step in the flow
    var currentStep by remember { mutableStateOf(CalendarStep.DATE_SELECTION) }

    // Initialize form fields with existing data or defaults
    var selectedDate by remember {
        mutableStateOf(todaysDate)
    }
    var selectedInterviewType by remember { mutableStateOf<InterviewType?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Initialize time from existing schedule or default to 9:00 AM
    var eventHours by remember { 
        val hours = 9
        mutableStateOf(hours) 
    }
    var eventMinutes by remember { 
        val minutes = 0
        mutableStateOf(minutes) 
    }
    var eventNotes by remember { mutableStateOf("") }
    var showInterviewTypeWarning by remember { mutableStateOf(false) }

    // Error states for past dates/times
    var showPastDateError by remember { mutableStateOf(false) }
    var showPastTimeError by remember { mutableStateOf(false) }

    // Format time as HH:MM for display and saving
    val formattedTime = remember(eventHours, eventMinutes) {
        val hours = eventHours.toString().padStart(2, '0')
        val minutes = eventMinutes.toString().padStart(2, '0')
        "$hours:$minutes"
    }

    // Effect to populate form fields when a scheduled date is selected
    LaunchedEffect(selectedDate) {
        // Check if the selected date matches the scheduled date
        if (existingSchedule != null && selectedDate == existingSchedule.date) {
            // Parse time from the scheduled date (format: "HH:MM")
            val timeParts = existingSchedule.time.split(":")
            if (timeParts.size == 2) {
                eventHours = timeParts[0].toIntOrNull() ?: 9
                eventMinutes = timeParts[1].toIntOrNull() ?: 0
            }

            // Set interview type from the scheduled date
            selectedInterviewType = existingSchedule.type

            // Set notes from the scheduled date
            eventNotes = existingSchedule.notes
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Calendar",
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    when (currentStep) {
                        CalendarStep.DATE_SELECTION -> "Select Date"
                        CalendarStep.TIME_SELECTION -> "Select Time"
                        CalendarStep.EVENT_SUMMARY -> "Event Summary"
                    }
                )
            }
        },
        text = {
            Column(modifier = Modifier.padding(8.dp)) {
                // Vacancy info - always visible
                Text(
                    text = vacancy.jobInfo.jobName,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "at ${vacancy.jobInfo.companyName}",
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Date Selection Step
                if (currentStep == CalendarStep.DATE_SELECTION) {
                    Column {
                    // Calendar view
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 360.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .shadow(4.dp, RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(8.dp)
                    ) {
                        // Parse the selected date or use default
                        val dateParts = selectedDate.split("-").map { it.toInt() }
                        val initialDate = if (dateParts.size == 3) {
                            CalendarDate(dateParts[0], dateParts[1], dateParts[2])
                        } else {
                            CalendarDate.now()
                        }

                        // Create a list of scheduled dates
                        val scheduledDates = listOfNotNull(vacancy.interviewSchedule?.date).filter { it.isNotEmpty() }

                        // Use our custom calendar component
                        CustomCalendar(
                            initialDate = initialDate,
                            onDateSelected = { date ->
                                selectedDate = date.format()
                                showPastDateError = false // Reset error when date changes
                            },
                            accentColor = accentColor,
                            scheduledDates = scheduledDates,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Next button to proceed to time selection
                    Button(
                        onClick = {
                            // Check if date is in the past
                            val today = getTodaysDate()
                            val isPastDate = selectedDate < today

                            if (isPastDate) {
                                showPastDateError = true
                            } else {
                                currentStep = CalendarStep.TIME_SELECTION
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = accentColor,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Next: Select Time")
                    }

                    // Show error if date is in the past
                    if (showPastDateError) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Error",
                                tint = errorColor
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Please select a future date",
                                color = errorColor
                            )
                        }
                    }
                    }
                }

                // Time Selection Step
                if (currentStep == CalendarStep.TIME_SELECTION) {
                    Column {
                    // Hour-based time selector component
                    HourlyTimeSelector(
                        selectedHour = eventHours,
                        hourlyEvents = emptyMap(),
                        onHourSelected = {
                            eventHours = it
                            showPastTimeError = false // Reset error when time changes
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Next button to proceed to event summary
                    Button(
                        onClick = {
                            // Check if time is in the past when date is today
                            val today = getTodaysDate()
                            val isPastTime = if (selectedDate == today) {
                                // Try to get current hour and minute from getTodaysDate()
                                // If it doesn't include time, use a default (current hour and 0 minutes)
                                var currentHour = 12 // Default to noon
                                var currentMinute = 0

                                try {
                                    val parts = getTodaysDate().split(" ")
                                    if (parts.size > 1) {
                                        val timeParts = parts[1].split(":")
                                        currentHour = timeParts[0].toInt()
                                        currentMinute = if (timeParts.size > 1) timeParts[1].toInt() else 0
                                    }
                                } catch (e: Exception) {
                                    // Fallback already set with default values
                                }

                                // Check if selected time is before current time
                                (eventHours < currentHour) ||
                                        (eventHours == currentHour && eventMinutes < currentMinute)
                            } else {
                                false
                            }

                            if (isPastTime) {
                                showPastTimeError = true
                            } else {
                                showPastTimeError = false
                                currentStep = CalendarStep.EVENT_SUMMARY
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = accentColor,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Next: Event Summary")
                    }

                    // Show error if time is in the past
                    if (showPastTimeError) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Error",
                                tint = errorColor
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Please select a future time",
                                color = errorColor
                            )
                        }
                    }
                    }
                }

                // Event Summary Step
                if (currentStep == CalendarStep.EVENT_SUMMARY) {
                    Column {
                    // Event summary card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = Color.White
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Event Summary",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Date: $selectedDate")
                            Text("Time: $formattedTime")

                            Spacer(modifier = Modifier.height(16.dp))

                            // Interview type selection
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    modifier = Modifier.fillMaxWidth(),
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
                                    onDismissRequest = { isDropdownExpanded = false }
                                ) {
                                    InterviewType.entries.forEach { interviewType ->
                                        DropdownMenuItem(
                                            onClick = {
                                                selectedInterviewType = interviewType
                                                isDropdownExpanded = false
                                                showInterviewTypeWarning = false
                                            }
                                        ) {
                                            Text(interviewType.displayName)
                                        }
                                    }
                                }
                            }

                            // Show warning if user tried to submit without selecting an interview type
                            if (showInterviewTypeWarning) {
                                Text(
                                    text = "Please select an interview type",
                                    color = errorColor,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Notes field
                            OutlinedTextField(
                                value = eventNotes,
                                onValueChange = { eventNotes = it },
                                label = { Text("Notes") },
                                placeholder = { Text("Any additional details...") },
                                modifier = Modifier.fillMaxWidth().height(80.dp),
                                maxLines = 3
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Back button to return to time selection
                    OutlinedButton(
                        onClick = { currentStep = CalendarStep.TIME_SELECTION },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back to Time Selection")
                    }
                    }
                }
            }
        },
        confirmButton = {
            // Only show confirm button in the event summary step
            if (currentStep == CalendarStep.EVENT_SUMMARY) {
                Button(
                    onClick = {
                        // Check if interview type is selected
                        if (selectedInterviewType == null) {
                            // Show warning if no interview type is selected
                            showInterviewTypeWarning = true
                        } else {
                            // Create the interview schedule
                            val interviewSchedule = InterviewSchedule(
                                date = selectedDate,
                                time = formattedTime,
                                type = selectedInterviewType,
                                notes = eventNotes
                            )

                            // Update the vacancy with the interview schedule
                            val updatedVacancy = vacancy.copy(
                                interviewSchedule = interviewSchedule
                            )

                            // Call onConfirm with the updated vacancy
                            onConfirm(updatedVacancy)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = accentColor,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(width = 120.dp, height = 44.dp)
                ) {
                    Text(if (vacancy.interviewSchedule != null) "Update Event" else "Save Event")
                }
            }
        },
        dismissButton = {
            // Show different dismiss buttons based on the current step
            when (currentStep) {
                CalendarStep.DATE_SELECTION -> {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(width = 100.dp, height = 44.dp)
                    ) {
                        Text("Cancel")
                    }
                }
                CalendarStep.TIME_SELECTION -> {
                    OutlinedButton(
                        onClick = { currentStep = CalendarStep.DATE_SELECTION },
                        modifier = Modifier.size(width = 100.dp, height = 44.dp)
                    ) {
                        Text("Back")
                    }
                }
                CalendarStep.EVENT_SUMMARY -> {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = errorColor
                        ),
                        modifier = Modifier.size(width = 100.dp, height = 44.dp)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    )
}
