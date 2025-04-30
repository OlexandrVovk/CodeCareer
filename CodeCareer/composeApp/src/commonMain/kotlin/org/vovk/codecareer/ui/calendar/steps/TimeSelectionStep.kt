package org.vovk.codecareer.ui.calendar.steps

import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vovk.codecareer.dal.enums.InterviewType

/**
 * Step 3: Event Summary
 */
@Composable
fun EventSummaryStep(
    selectedDate: String,
    formattedTime: String,
    selectedInterviewType: InterviewType?,
    isDropdownExpanded: Boolean,
    showInterviewTypeWarning: Boolean,
    errorColor: Color,
    accentColor: Color,
    eventNotes: String,
    isUpdate: Boolean,
    onDropdownToggle: () -> Unit,
    onInterviewTypeSelected: (InterviewType) -> Unit,
    onNotesChange: (String) -> Unit,
    onBack: () -> Unit, // Added back callback
    onCancel: () -> Unit, // Added cancel callback
    onConfirm: () -> Unit // Added confirm callback
) {
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Event Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(8.dp))
                Text("Date: $selectedDate")
                Text("Time: $formattedTime")
                Spacer(Modifier.height(16.dp))
                // Interview type dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onDropdownToggle
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(selectedInterviewType?.displayName ?: "Select interview type", modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                        }
                    }
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = onDropdownToggle
                    ) {
                        InterviewType.entries.forEach { t ->
                            DropdownMenuItem(onClick = { onInterviewTypeSelected(t) }) {
                                Text(t.displayName)
                            }
                        }
                    }
                    if (showInterviewTypeWarning) {
                        Text("Please select an interview type", color = errorColor, modifier = Modifier.padding(top = 4.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = eventNotes,
                    onValueChange = onNotesChange,
                    label = { Text("Notes") },
                    placeholder = { Text("Any additional details…") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    maxLines = 3
                )
            }
        }

        // Add navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left side - Back and Cancel buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.size(width = 100.dp, height = 44.dp)
                ) {
                    Text("Back")
                }

                OutlinedButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = errorColor
                    ),
                    modifier = Modifier.size(width = 100.dp, height = 44.dp)
                ) {
                    Text("Cancel")
                }
            }

            // Right side - Confirm button
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = accentColor,
                    contentColor = Color.White
                ),
                modifier = Modifier.size(width = 120.dp, height = 44.dp)
            ) {
                Text(if (isUpdate) "Update Event" else "Save Event")
            }
        }
    }
}