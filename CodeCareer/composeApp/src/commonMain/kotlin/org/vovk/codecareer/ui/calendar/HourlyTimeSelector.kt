package org.vovk.codecareer.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Hour-based time selector showing 24 rows.
 *
 * @param focusHour the hour to focus/scroll to initially (0-23)
 * @param hourlyEvents a map from hour to event title/summary
 * @param onHourSelected callback when the user taps an hour row
 * @param modifier Modifier for layout adjustments
 */
@Composable
fun HourlyTimeSelector(
    focusHour: Int,
    hourlyEvents: Map<Int, String> = emptyMap(),
    onHourSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Initialize list state to center the focus hour
    val halfWindow = 3
    val initialIndex = (focusHour - halfWindow).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    Column(modifier = modifier.fillMaxWidth()) {
        Divider()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            itemsIndexed((0..23).toList()) { _, hour ->
                // Format hour as two digits (e.g., "09:00")
                val label = hour.toString().padStart(2, '0') + ":00"
                val eventText = hourlyEvents[hour].orEmpty()
                val isSelected = hour == focusHour
                val hasEvent = eventText.isNotEmpty()
                // Highlight selection or scheduled events
                val bgColor = when {
                    hasEvent && isSelected ->  Color(0xFFFFD700).copy(alpha = 0.2f)
                    isSelected -> MaterialTheme.colors.primary.copy(alpha = 0.1f)
                    hasEvent ->  Color(0xFFFFD700).copy(alpha = 0.2f)
                    else -> Color.Transparent
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bgColor)
                        .clickable { onHourSelected(hour) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        fontSize = 16.sp,
                        modifier = Modifier.width(60.dp),
                        color = Color(199, 194, 200)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = eventText,
                        fontSize = 14.sp,
                        color = Color(199, 194, 200)
                    )
                }
                Divider()
            }
        }
    }
}