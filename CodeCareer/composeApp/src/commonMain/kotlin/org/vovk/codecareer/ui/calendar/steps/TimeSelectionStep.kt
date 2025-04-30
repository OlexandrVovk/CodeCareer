package org.vovk.codecareer.ui.calendar.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vovk.codecareer.ui.calendar.HourlyTimeSelector

/**
 * Step 2: Time Selection (hourly list)
 */
@Composable
fun TimeSelectionStep(
    selectedHour: Int,
    showPastTimeError: Boolean,
    errorColor: Color,
    onHourSelected: (Int) -> Unit
) {
    Column{
        HourlyTimeSelector(
            selectedHour = selectedHour,
            hourlyEvents = emptyMap(),
            onHourSelected = onHourSelected,
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
    }
}