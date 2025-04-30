package org.vovk.codecareer.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.vovk.codecareer.dal.entities.InterviewSchedule
import org.vovk.codecareer.dal.entities.TrackedVacancy
import org.vovk.codecareer.ui.calendar.steps.DateSelectionStep
import org.vovk.codecareer.ui.calendar.steps.EventSummaryStep
import org.vovk.codecareer.ui.calendar.steps.TimeSelectionStep

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

    // Function to handle save/update event
    val handleConfirm = {
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
                    DateSelectionStep(
                        vacancy = vacancy,
                        currentDate = selectedDate,
                        accentColor = accentColor,
                        errorColor = errorColor,
                        showPastDateError = showPastDateError,
                        onDateSelected = { date ->
                            selectedDate = date
                            showPastDateError = false
                            // Automatically advance to next step after date selection
                            currentStep = CalendarStep.TIME_SELECTION
                        },
                        onCancel = onDismiss
                    )
                }

                // Time Selection Step
                if (currentStep == CalendarStep.TIME_SELECTION) {
                    TimeSelectionStep(
                        startingHour = eventHours,
                        showPastTimeError = showPastTimeError,
                        errorColor = errorColor,
                        onHourSelected = {
                            eventHours = it
                            showPastTimeError = false
                            // Automatically advance to next step after time selection
                            currentStep = CalendarStep.EVENT_SUMMARY
                        },
                        onBack = { currentStep = CalendarStep.DATE_SELECTION },
                        onCancel = onDismiss
                    )
                }

                // Event Summary Step
                if (currentStep == CalendarStep.EVENT_SUMMARY) {
                    EventSummaryStep(
                        selectedDate = selectedDate,
                        formattedTime = formattedTime,
                        selectedInterviewType = selectedInterviewType,
                        isDropdownExpanded = isDropdownExpanded,
                        showInterviewTypeWarning = showInterviewTypeWarning,
                        errorColor = errorColor,
                        accentColor = accentColor,
                        eventNotes = eventNotes,
                        isUpdate = vacancy.interviewSchedule != null,
                        onDropdownToggle = { isDropdownExpanded = !isDropdownExpanded },
                        onInterviewTypeSelected = { interviewType ->
                            selectedInterviewType = interviewType
                            isDropdownExpanded = false
                            showInterviewTypeWarning = false
                        },
                        onNotesChange = { eventNotes = it },
                        onBack = { currentStep = CalendarStep.TIME_SELECTION },
                        onCancel = onDismiss,
                        onConfirm = handleConfirm
                    )
                }
            }
        },
        // We no longer need these buttons as they're now in the steps
        confirmButton = { },
        dismissButton = { }
    )
}