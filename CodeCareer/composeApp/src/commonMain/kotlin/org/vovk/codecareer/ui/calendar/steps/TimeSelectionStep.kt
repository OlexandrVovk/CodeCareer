package org.vovk.codecareer.ui.calendar.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vovk.codecareer.ui.calendar.HourlyTimeSelector
import org.vovk.codecareer.dal.entities.InterviewSchedule

/**
 * Step 2: Time Selection (hourly list)
 */
@Composable
fun TimeSelectionStep(
    startingHour: Int,
    scheduledSchedules: List<InterviewSchedule> = emptyList(),
    showPastTimeError: Boolean,
    errorColor: Color,
    onHourSelected: (Int) -> Unit,
    onBack: () -> Unit, // Added back callback
    onCancel: () -> Unit // Added cancel callback
) {
    var selectedHour by remember { mutableStateOf(startingHour) }
    // Map scheduled meetings on this date to hours -> display text
    val hourlyEvents = remember(scheduledSchedules) {
        scheduledSchedules.mapNotNull { schedule ->
            schedule.time.split(":").firstOrNull()?.toIntOrNull()?.let { hour ->
                hour to (schedule.type?.name ?: "Meeting")
            }
        }.toMap()
    }
    Column {
        HourlyTimeSelector(
            focusHour = selectedHour,
            hourlyEvents = hourlyEvents,
            onHourSelected = { hour ->
                // Update selection
                selectedHour = hour
                // If there's a meeting at this hour, proceed immediately
                if (hourlyEvents.containsKey(hour)) {
                    onHourSelected(hour)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        if (showPastTimeError) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = errorColor)
                Spacer(Modifier.width(4.dp))
                Text("Please select a future time", color = errorColor, fontSize = 14.sp)
            }
        }

        // Add navigation buttons
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
                onClick = { onHourSelected(selectedHour) },
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
        }
    }
}