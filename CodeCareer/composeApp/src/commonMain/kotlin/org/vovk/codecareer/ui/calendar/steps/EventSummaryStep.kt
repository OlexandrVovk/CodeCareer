package org.vovk.codecareer.ui.calendar.steps

import androidx.compose.foundation.layout.*
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
    eventNotes: String,
    onDropdownToggle: () -> Unit,
    onInterviewTypeSelected: (InterviewType) -> Unit,
    onNotesChange: (String) -> Unit,
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
    }
}