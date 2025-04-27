package org.vovk.codecareer.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.ceil


/**
 * Simple date class for our calendar
 */
data class CalendarDate(
    val year: Int,
    val month: Int,
    val day: Int
) {
    companion object {
        // Get current date (simplified, assumes current date is June 15, 2023)
        fun now(): CalendarDate = CalendarDate(2023, 6, 15)

        // Format date as string
        fun formatDate(date: CalendarDate): String {
            val monthStr = if (date.month < 10) "0${date.month}" else date.month.toString()
            val dayStr = if (date.day < 10) "0${date.day}" else date.day.toString()
            return "${date.year}-$monthStr-$dayStr"
        }
    }

    // Get day of week for this date (0 = Sunday, 6 = Saturday)
    // This is a simplified implementation using Zeller's Congruence algorithm
    fun dayOfWeek(): Int {
        val m = if (month < 3) month + 12 else month
        val y = if (month < 3) year - 1 else year
        val h = (day +
                (13 * (m + 1)) / 5 +
                y + y / 4 -
                y / 100 +
                y / 400) % 7
        return (h + 6) % 7
    }

    // Format this date as string
    fun format(): String = formatDate(this)
}

/**
 * Enum for months
 */
enum class CalendarMonth(val number: Int, val displayName: String, val daysInMonth: Int) {
    JANUARY(1, "January", 31),
    FEBRUARY(2, "February", 28), // Will be adjusted for leap years
    MARCH(3, "March", 31),
    APRIL(4, "April", 30),
    MAY(5, "May", 31),
    JUNE(6, "June", 30),
    JULY(7, "July", 31),
    AUGUST(8, "August", 31),
    SEPTEMBER(9, "September", 30),
    OCTOBER(10, "October", 31),
    NOVEMBER(11, "November", 30),
    DECEMBER(12, "December", 31);

    companion object {
        fun fromNumber(number: Int): CalendarMonth {
            return values().find { it.number == number } ?: JANUARY
        }

        fun next(month: CalendarMonth): CalendarMonth {
            return if (month == DECEMBER) JANUARY else fromNumber(month.number + 1)
        }

        fun previous(month: CalendarMonth): CalendarMonth {
            return if (month == JANUARY) DECEMBER else fromNumber(month.number - 1)
        }
    }

    // Get actual days in month, accounting for leap years
    fun getDaysInMonth(year: Int): Int {
        return if (this == FEBRUARY && isLeapYear(year)) 29 else daysInMonth
    }
}

/**
 * Function to check if a year is a leap year
 */
private fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}

/**
 * A custom calendar component for Jetpack Compose.
 * 
 * @param initialDate The initially selected date
 * @param onDateSelected Callback when a date is selected
 * @param accentColor The accent color for selected dates and highlights
 * @param modifier Modifier for the calendar container
 */
@Composable
fun CustomCalendar(
    initialDate: CalendarDate = CalendarDate.now(),
    onDateSelected: (CalendarDate) -> Unit,
    accentColor: Color = Color(0xFF864AED),
    scheduledDates: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    // State for the currently displayed month and year
    var currentMonth by remember { mutableStateOf(CalendarMonth.fromNumber(initialDate.month)) }
    var currentYear by remember { mutableStateOf(initialDate.year) }

    // State for the selected date
    var selectedDate by remember { mutableStateOf(initialDate) }

    // Calculate the first day of the month
    val firstDayOfMonth = CalendarDate(currentYear, currentMonth.number, 1)

    val rawFirst = firstDayOfMonth.dayOfWeek()
    val firstDayOfWeek = (rawFirst + 6) % 7

    // Calculate the number of days in the month
    val daysInMonth = currentMonth.getDaysInMonth(currentYear)

    // Calculate the number of rows needed (including partial weeks)
    val rows = ceil((firstDayOfWeek + daysInMonth) / 7.0).toInt()

    Column(modifier = modifier) {
        // Month and year header with navigation
        MonthYearHeader(
            month = currentMonth,
            year = currentYear,
            onPreviousMonth = {
                if (currentMonth == CalendarMonth.JANUARY) {
                    currentMonth = CalendarMonth.DECEMBER
                    currentYear -= 1
                } else {
                    currentMonth = CalendarMonth.previous(currentMonth)
                }
            },
            onNextMonth = {
                if (currentMonth == CalendarMonth.DECEMBER) {
                    currentMonth = CalendarMonth.JANUARY
                    currentYear += 1
                } else {
                    currentMonth = CalendarMonth.next(currentMonth)
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Days of week header
        DaysOfWeekHeader()

        Spacer(modifier = Modifier.height(4.dp))

        // Calendar grid
        CalendarGrid(
            firstDayOfWeek = firstDayOfWeek,
            daysInMonth = daysInMonth,
            rows = rows,
            selectedDate = selectedDate,
            currentMonth = currentMonth,
            currentYear = currentYear,
            accentColor = accentColor,
            scheduledDates = scheduledDates,
            onDateSelected = { date ->
                selectedDate = date
                onDateSelected(date)
            }
        )
    }
}

/**
 * Header showing the current month and year with navigation buttons.
 */
@Composable
private fun MonthYearHeader(
    month: CalendarMonth,
    year: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${month.displayName} $year",
            fontWeight = FontWeight.Bold
        )
        Row {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous month"
                )
            }
            IconButton(onClick = onNextMonth) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next month"
                )
            }
        }
    }
}

/**
 * Header showing the days of the week.
 */
@Composable
private fun DaysOfWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        val daysOfWeek = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
/**
 * Grid of days for the current month.
 */
@Composable
private fun CalendarGrid(
    firstDayOfWeek: Int,
    daysInMonth: Int,
    rows: Int,
    selectedDate: CalendarDate,
    currentMonth: CalendarMonth,
    currentYear: Int,
    accentColor: Color,
    scheduledDates: List<String> = emptyList(),
    onDateSelected: (CalendarDate) -> Unit
) {
    val prevMonth = CalendarMonth.previous(currentMonth)
    val prevYear = if (currentMonth == CalendarMonth.JANUARY) currentYear - 1 else currentYear
    val daysInPrevMonth = prevMonth.getDaysInMonth(prevYear)

    val nextMonth = CalendarMonth.next(currentMonth)
    val nextYear = if (currentMonth == CalendarMonth.DECEMBER) currentYear + 1 else currentYear
    Column {
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (column in 0 until 7) {
                    val linearIndex = row * 7 + column
                    val dayOffset = linearIndex + 1 - firstDayOfWeek

                    when {
                        dayOffset in 1..daysInMonth -> {
                            val date = CalendarDate(currentYear, currentMonth.number, dayOffset)
                            val dateStr = date.format()
                            val isDateScheduled = scheduledDates.contains(dateStr)
                            DayCell(
                                day = dayOffset,
                                isSelected = date == selectedDate,
                                isScheduled = isDateScheduled,
                                accentColor = accentColor,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp),
                                onClick = { onDateSelected(date) }
                            )
                        }
                        dayOffset < 1 -> {
                            val prevDay = daysInPrevMonth + dayOffset
                            val prevDate = CalendarDate(prevYear, prevMonth.number, prevDay)
                            val prevDateStr = prevDate.format()
                            val isPrevDateScheduled = scheduledDates.contains(prevDateStr)
                            DayCell(
                                day = prevDay,
                                isSelected = false,
                                isScheduled = isPrevDateScheduled,
                                accentColor = Color.Gray,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp),
                                onClick = {
                                    onDateSelected(prevDate)
                                }
                            )
                        }

                        else -> {
                            val nextDay = dayOffset - daysInMonth
                            val nextDate = CalendarDate(nextYear, nextMonth.number, nextDay)
                            val nextDateStr = nextDate.format()
                            val isNextDateScheduled = scheduledDates.contains(nextDateStr)
                            DayCell(
                                day = nextDay,
                                isSelected = false,
                                isScheduled = isNextDateScheduled,
                                accentColor = Color.Gray,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp),
                                onClick = {
                                    onDateSelected(nextDate)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual day cell in the calendar.
 */
@Composable
private fun DayCell(
    day: Int,
    isSelected: Boolean,
    isScheduled: Boolean = false,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                when {
                    isSelected -> accentColor.copy(alpha = 0.2f)
                    isScheduled -> Color(0xFFFFD700).copy(alpha = 0.2f) // Light gold for scheduled dates
                    else -> Color.Transparent
                }
            )
            .border(
                width = when {
                    isSelected -> 1.dp
                    isScheduled -> 1.dp
                    else -> 0.dp
                },
                color = when {
                    isSelected -> accentColor
                    isScheduled -> Color(0xFFFFD700) // Gold for scheduled dates
                    else -> Color.Transparent
                },
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = when {
                isSelected -> accentColor
                isScheduled -> Color(0xFFB8860B) // Dark gold for scheduled dates text
                else -> Color.Unspecified
            },
            fontWeight = when {
                isSelected -> FontWeight.Bold
                isScheduled -> FontWeight.Bold
                else -> FontWeight.Normal
            }
        )
    }
}
