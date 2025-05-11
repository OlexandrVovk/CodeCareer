package org.vovk.codecareer.ui.calendar.steps

//import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.material.TextFieldDefaults
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
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onDeleteMeeting: () -> Unit, // Delete specific meeting
    onConfirm: () -> Unit
) {
    Column {
        Card(
            backgroundColor = Color(17, 18, 20, 255),
            contentColor = Color(199, 194, 200),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Event Summary",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Date: $selectedDate",
                    color = Color(199, 194, 200)
                )
                Text(
                    "Time: $formattedTime",
                    color = Color(199, 194, 200)
                )
                Spacer(Modifier.height(16.dp))
                // Interview type dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onDropdownToggle,
                        modifier = Modifier
                            .wrapContentWidth()
                            .border(
                                width = 2.dp,
                                color = Color(57,60,64,255),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(17,18,20,255)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                selectedInterviewType?.displayName ?: "Select interview type",
                                color = Color.White
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = Color.White
                            )
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
                    label = { Text("Notes", color = Color(199, 194, 200)) },
                    placeholder = { Text("Any additional details…", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    maxLines = 3,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.White,
                        cursorColor = accentColor,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color.White,
                        backgroundColor = Color(17, 18, 20, 255),
                        placeholderColor = Color.Gray,
                        focusedLabelColor = accentColor,
                        unfocusedLabelColor = Color.White
                    )
                )
            }
        }

        // Add navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
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
            if (isUpdate) {
                OutlinedButton(
                    onClick = onDeleteMeeting,
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
            OutlinedButton(
                onClick = onConfirm,
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
                Text(if (isUpdate) "Update" else "Save")
            }
        }
    }
}