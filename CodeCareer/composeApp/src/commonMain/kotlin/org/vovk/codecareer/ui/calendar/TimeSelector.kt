package org.vovk.codecareer.ui.calendar

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * A wheel picker component for time selection with iOS-like appearance.
 * 
 * @param hours Current hour value (0-23)
 * @param minutes Current minute value (must be a multiple of 5)
 * @param onHoursChange Callback when hours value changes
 * @param onMinutesChange Callback when minutes value changes
 * @param onBackClick Callback when back button is clicked
 * @param modifier Modifier for the component
 */
@Composable
fun TimeSelector(
    hours: Int,
    minutes: Int,
    onHoursChange: (Int) -> Unit,
    onMinutesChange: (Int) -> Unit,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val accentColor = Color(0xFF864AED)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5F5F5).copy(alpha = 0.9f))
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Time picker wheels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hours wheel
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                WheelPicker(
                    items = (0..23).map { it.toString().padStart(2, '0') },
                    selectedIndex = hours,
                    onValueChange = { onHoursChange(it) }
                )
            }

            // Separator
            Text(
                text = ":",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Minutes wheel (5-minute increments)
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                WheelPicker(
                    items = (0..11).map { (it * 5).toString().padStart(2, '0') },
                    selectedIndex = minutes / 5,
                    onValueChange = { onMinutesChange(it * 5) }
                )
            }
        }

        // Selection indicator (translucent bar in the middle)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .offset(y = (-120).dp)
                .background(accentColor.copy(alpha = 0.1f))
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

/**
 * A wheel picker component that displays a scrollable list of items.
 * 
 * @param items List of items to display
 * @param selectedIndex Currently selected index
 * @param onValueChange Callback when selected value changes
 */
@Composable
private fun WheelPicker(
    items: List<String>,
    selectedIndex: Int,
    onValueChange: (Int) -> Unit
) {
    val visibleItems = 9 // Number of visible items (should be odd)
    val itemHeight = 40.dp
    val halfVisibleItems = visibleItems / 2

    var scrollOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // Update value dynamically during scrolling
    LaunchedEffect(scrollOffset) {
        val targetIndex = (selectedIndex + (scrollOffset / itemHeight.value).roundToInt())
            .coerceIn(0, items.size - 1)
        if (targetIndex != selectedIndex) {
            onValueChange(targetIndex)
        }

        // Reset offset when drag ends
        if (!isDragging && scrollOffset != 0f) {
            scrollOffset = 0f
        }
    }

    Box(
        modifier = Modifier
            .height(itemHeight * visibleItems)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { isDragging = false },
                    onDragCancel = { isDragging = false },
                    onVerticalDrag = { _, dragAmount ->
                        scrollOffset += dragAmount / 2 // Reduce sensitivity

                        // If scrolling would move beyond bounds, apply resistance
                        val targetIndex = selectedIndex + (scrollOffset / itemHeight.value).roundToInt()
                        if (targetIndex < 0 || targetIndex >= items.size) {
                            scrollOffset /= 2 // Apply resistance
                        }
                    }
                )
            }
    ) {
        // Draw the items
        for (i in -halfVisibleItems..halfVisibleItems) {
            val index = selectedIndex + i
            if (index in items.indices) {
                val offset = i * itemHeight.value + scrollOffset
                val absoluteOffset = abs(offset)

                // Calculate scale and alpha based on distance from center
                val scale = 1f - (absoluteOffset / (itemHeight.value * (halfVisibleItems + 1))) * 0.3f
                val alpha = 1f - (absoluteOffset / (itemHeight.value * (halfVisibleItems + 1))) * 0.7f

                val animatedScale by animateFloatAsState(
                    targetValue = scale,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "scale"
                )

                val density = LocalDensity.current
                val offsetDp = with(density) { (scrollOffset / density.density).dp }
                Box(
                    modifier = Modifier
                        .offset(y = itemHeight * i + offsetDp)
                        .height(itemHeight)
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = animatedScale
                            scaleY = animatedScale
                        }
                        .alpha(alpha)
                        .blur(if (i == 0) 0.dp else 1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[index],
                        fontSize = 20.sp,
                        fontWeight = if (i == 0) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = if (i == 0) Color.Black else Color.Gray
                    )
                }
            }
        }
    }
}
