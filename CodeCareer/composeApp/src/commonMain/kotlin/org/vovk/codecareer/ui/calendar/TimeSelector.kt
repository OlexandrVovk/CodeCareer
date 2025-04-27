package org.vovk.codecareer.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun TimeSelector(
    hours: Int,
    minutes: Int,
    onHoursChange: (Int) -> Unit,
    onMinutesChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hours section
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Hours")
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (hours > 0) onHoursChange(hours - 1)
                    },
                    modifier = Modifier.size(40 .dp)
                ) {
                    Text("-")
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = hours.toString(),
                    onValueChange = { newValue ->
                        val newHours = newValue.toIntOrNull() ?: 0
                        if (newHours in 0..23) {
                            onHoursChange(newHours)
                        }
                    },
                    modifier = Modifier.width(60.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (hours < 23) onHoursChange(hours + 1) else onHoursChange(0)
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Text("+")
                }
            }
        }
        // Minutes section
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Minutes")
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (minutes > 0) onMinutesChange(minutes - 1)
                        else onMinutesChange(59)
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Text("-")
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = minutes.toString(),
                    onValueChange = { newValue ->
                        val newMinutes = newValue.toIntOrNull() ?: 0
                        if (newMinutes in 0..59) {
                            onMinutesChange(newMinutes)
                        }
                    },
                    modifier = Modifier.width(60.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (minutes < 59) onMinutesChange(minutes + 1)
                        else onMinutesChange(0)
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Text("+")
                }
            }
        }
    }
}