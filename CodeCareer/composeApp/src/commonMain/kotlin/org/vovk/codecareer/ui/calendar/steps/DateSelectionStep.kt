package org.vovk.codecareer.ui.calendar.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.material.ButtonDefaults
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vovk.codecareer.dal.entities.TrackedVacancy
import org.vovk.codecareer.ui.calendar.CalendarDate
import org.vovk.codecareer.ui.calendar.CustomCalendar

/**
 * Step 1: Date Selection
 */
@Composable
fun DateSelectionStep(
    vacancy: TrackedVacancy,
    currentDate: String,
    accentColor: Color,
    errorColor: Color,
    showPastDateError: Boolean,
    onDateSelected: (String) -> Unit,
    onCancel: () -> Unit,
    onDeleteMeeting: (String) -> Unit
) {
    var selectedDate by remember { mutableStateOf(currentDate)}
    Column(modifier = Modifier.padding(8.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 360.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(17, 18, 20, 255))
                .padding(8.dp)
        ) {
            // Build initial date
            val parts = currentDate.split("-").mapNotNull { it.toIntOrNull() }
            val initial = if (parts.size == 3) CalendarDate(parts[0], parts[1], parts[2]) else CalendarDate.Companion.now()
            // Existing scheduled dates (allow multiple)
            val scheduled = vacancy.interviewSchedules.map { it.date }.filter { it.isNotEmpty() }
            CustomCalendar(
                initialDate = initial,
                onDateSelected = { d -> selectedDate = d.format() },
                accentColor = accentColor,
                scheduledDates = scheduled,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(16.dp))

        if (showPastDateError) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = errorColor)
                Spacer(Modifier.width(4.dp))
                Text("Please select a future date", color = errorColor, fontSize = 14.sp)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .size(width = 100.dp, height = 44.dp)
                    .border(
                        width = 2.dp,
                        color = Color(57,60,64,255),
                        shape = RoundedCornerShape(8.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(17,18,20,255)
                )
            ) {
                Text("Cancel")
            }
            OutlinedButton(
                onClick = { onDateSelected(selectedDate) },
                modifier = Modifier
                    .size(width = 100.dp, height = 44.dp)
                    .border(
                        width = 2.dp,
                        color = Color(57,60,64,255),
                        shape = RoundedCornerShape(8.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(17,18,20,255)
                )
            ) {
                Text("Next")
            }
            // Show delete button if a meeting is scheduled on the selected date
            if (vacancy.interviewSchedules.any { it.date == selectedDate }) {
                OutlinedButton(
                    onClick = { onDeleteMeeting(selectedDate) },
                    modifier = Modifier
                        .size(width = 100.dp, height = 44.dp)
                        .border(
                            width = 2.dp,
                            color = Color(57,60,64,255),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(17,18,20,255)
                    )
                ) {
                    Text("Delete")
                }
            }
        }
    }
}