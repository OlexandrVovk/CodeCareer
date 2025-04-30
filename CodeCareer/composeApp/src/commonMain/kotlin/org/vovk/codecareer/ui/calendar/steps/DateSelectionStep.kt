package org.vovk.codecareer.ui.calendar.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    selectedDate: String,
    accentColor: Color,
    errorColor: Color,
    showPastDateError: Boolean,
    onDateSelected: (String) -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 360.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(8.dp)
        ) {
            // Build initial date
            val parts = selectedDate.split("-").mapNotNull { it.toIntOrNull() }
            val initial = if (parts.size == 3) CalendarDate(parts[0], parts[1], parts[2]) else CalendarDate.Companion.now()
            // Existing scheduled dates
            val scheduled = listOfNotNull(vacancy.interviewSchedule?.date).filter { it.isNotEmpty() }
            CustomCalendar(
                initialDate = initial,
                onDateSelected = { d -> onDateSelected(d.format()) },
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
    }
}