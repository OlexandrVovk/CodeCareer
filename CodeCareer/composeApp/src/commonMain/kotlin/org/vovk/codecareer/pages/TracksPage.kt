package org.vovk.codecareer.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.vovk.codecareer.dal.entities.TrackedVacancy
import org.vovk.codecareer.dal.enums.VacancyStatus
import org.vovk.codecareer.dal.firebase.FirebaseManager
import org.vovk.codecareer.dal.firebase.UserSessionManager
import org.vovk.codecareer.pages.auth.LoginPage
import org.vovk.codecareer.ui.calendar.CalendarDialog

class TracksPage : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val firebaseManager = remember { FirebaseManager() }
        val coroutineScope = rememberCoroutineScope()

        // State for tracked vacancies
        var trackedVacancies by remember { mutableStateOf<List<TrackedVacancy>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        // State for delete confirmation dialog
        var showDeleteDialog by remember { mutableStateOf(false) }
        var vacancyToDelete by remember { mutableStateOf<TrackedVacancy?>(null) }

        // State for calendar dialog
        var showCalendarDialog by remember { mutableStateOf(false) }
        var vacancyToSchedule by remember { mutableStateOf<TrackedVacancy?>(null) }

        // Screen width detection for responsive layout
        var screenWidth by remember { mutableStateOf(0.dp) }
        BoxWithConstraints {
            screenWidth = maxWidth
        }

        // Determine if we should use table or card layout
        val isTabletOrMobile = screenWidth < 768.dp

        // Fetch tracked vacancies when the page loads
        LaunchedEffect(Unit) {
            if (!UserSessionManager.isLoggedIn()) {
                navigator.push(LoginPage())
                return@LaunchedEffect
            }

            firebaseManager.toGetTrackedVacancies { vacancies ->
                trackedVacancies = vacancies
                isLoading = false
            }
        }

        // Delete confirmation dialog
        if (showDeleteDialog && vacancyToDelete != null) {
            DeleteConfirmationDialog(
                vacancy = vacancyToDelete!!,
                onConfirm = {
                    trackedVacancies = trackedVacancies.filter { it != vacancyToDelete }
                    firebaseManager.toDeleteTrackedVacancy(vacancyToDelete!!)
                    vacancyToDelete = null
                    showDeleteDialog = false
                },
                onDismiss = {
                    vacancyToDelete = null
                    showDeleteDialog = false
                }
            )
        }

        // Calendar dialog
        if (showCalendarDialog && vacancyToSchedule != null) {
            CalendarDialog(
                vacancy = vacancyToSchedule!!,
                onConfirm = { updatedVacancy ->
                    vacancyToSchedule = updatedVacancy
                    firebaseManager.toScheduleInterview(updatedVacancy)
                    trackedVacancies = trackedVacancies.map {
                        if (it.jobInfo.jobUrl == updatedVacancy.jobInfo.jobUrl) updatedVacancy else it
                    }
                    vacancyToSchedule = null
                    showCalendarDialog = false
                },
                onDeleteMeeting = { deletedVacancy, schedule ->
                    // Delete the specific meeting in Firebase
                    firebaseManager.toDeleteMeeting(deletedVacancy, schedule)
                    // Update the local list to remove the meeting
                    trackedVacancies = trackedVacancies.map {
                        if (it.jobInfo.jobUrl == deletedVacancy.jobInfo.jobUrl) {
                            it.copy(interviewSchedules = it.interviewSchedules.filter { it != schedule })
                        } else it
                    }
                    vacancyToSchedule = null
                    showCalendarDialog = false
                },
                onDismiss = {
                    vacancyToSchedule = null
                    showCalendarDialog = false
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "My Tracked Vacancies",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = Color(199,194,200)
            )

            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading) {
                // Show loading indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                // Show error message
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            errorMessage ?: "An error occurred",
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = {
                            isLoading = true
                            errorMessage = null
                            firebaseManager.toGetTrackedVacancies { vacancies ->
                                trackedVacancies = vacancies
                                isLoading = false
                            }
                        }) {
                            Text("Try Again")
                        }
                    }
                }
            } else {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.dp,
                        color = Color(84, 85, 86, 255),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(17,18,20,255))
                    .padding(16.dp)
                ) {
                    if (!isTabletOrMobile) {
                        // Desktop layout - Table view
                        // Table header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(17,18,20,255))
                                .padding(8.dp)
                        ) {
                            Text(
                                "Vacancy",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                color = Color(199,194,200)
                            )
                            Text(
                                "Status",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                color = Color(199,194,200)
                            )
                            Text(
                                "Notes",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(2f),
                                color = Color(199,194,200)
                            )
                            // Added column for actions
                            Text(
                                "Actions",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(50.dp),
                                textAlign = TextAlign.Center,
                                color = Color(199,194,200)
                            )
                        }

                        // Table content
                        if (trackedVacancies.isEmpty()) {
                            EmptyTrackedVacanciesMessage()
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(17,18,20,255))
                            ) {
                                items(trackedVacancies) { vacancy ->
                                    VacancyRow(
                                        vacancy = vacancy,
                                        onStatusChange = { newStatus ->
                                            updateVacancyStatus(vacancy, newStatus, trackedVacancies) { updatedList ->
                                                trackedVacancies = updatedList
                                                firebaseManager.toUpdateTrackedVacancy(
                                                    updatedList.first { it.jobInfo.jobUrl == vacancy.jobInfo.jobUrl }
                                                )
                                            }
                                        },
                                        onNotesChange = { newNotes ->
                                            updateVacancyNotes(vacancy, newNotes, trackedVacancies) { updatedList ->
                                                trackedVacancies = updatedList
                                                firebaseManager.toUpdateTrackedVacancy(
                                                    updatedList.first { it.jobInfo.jobUrl == vacancy.jobInfo.jobUrl }
                                                )
                                            }
                                        },
                                        onDeleteClick = {
                                            vacancyToDelete = vacancy
                                            showDeleteDialog = true
                                        },
                                        onCalendarClick = {
                                            vacancyToSchedule = vacancy
                                            showCalendarDialog = true
                                        }
                                    )
                                    Divider()
                                }
                            }
                        }
                    }
                    else {
                        // Mobile/Tablet layout - Card view with swipe to delete
                        if (trackedVacancies.isEmpty()) {
                            EmptyTrackedVacanciesMessage()
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(17,18,20,255)),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = trackedVacancies,
                                    key = { it.jobInfo.jobUrl }
                                ) { vacancy ->
                                    var isVisible by remember { mutableStateOf(true) }
                                    var offsetX by remember { mutableStateOf(0f) }
                                    val deleteThreshold = -200f

                                    AnimatedVisibility(
                                        visible = isVisible,
                                        exit = slideOutHorizontally(
                                            targetOffsetX = { -it },
                                            animationSpec = tween(durationMillis = 300)
                                        )
                                    ) {
                                        VacancyCard(
                                            vacancy = vacancy,
                                            offsetX = offsetX,
                                            onStatusChange = { newStatus ->
                                                updateVacancyStatus(vacancy, newStatus, trackedVacancies) { updatedList ->
                                                    trackedVacancies = updatedList
                                                    firebaseManager.toUpdateTrackedVacancy(
                                                        updatedList.first { it.jobInfo.jobUrl == vacancy.jobInfo.jobUrl }
                                                    )
                                                }
                                            },
                                            onNotesChange = { newNotes ->
                                                updateVacancyNotes(vacancy, newNotes, trackedVacancies) { updatedList ->
                                                    trackedVacancies = updatedList
                                                    firebaseManager.toUpdateTrackedVacancy(
                                                        updatedList.first { it.jobInfo.jobUrl == vacancy.jobInfo.jobUrl }
                                                    )
                                                }
                                            },
                                            onDrag = { delta ->
                                                // Only allow dragging to the left (negative values)
                                                if (offsetX + delta <= 0) {
                                                    offsetX += delta
                                                }
                                            },
                                            onDragEnd = {
                                                if (offsetX < deleteThreshold) {
                                                    // Trigger delete animation
                                                    isVisible = false
                                                    coroutineScope.launch {
                                                        delay(300) // Wait for animation to complete
                                                        trackedVacancies = trackedVacancies.filter { it != vacancy }
                                                        firebaseManager.toDeleteTrackedVacancy(vacancy)
                                                    }
                                                } else {
                                                    // Snap back
                                                    offsetX = 0f
                                                }
                                            }
                                        )
                                    }

                                    Divider(
                                        color = Color(84, 85, 86, 255),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateVacancyStatus(
        vacancy: TrackedVacancy,
        newStatus: VacancyStatus,
        currentList: List<TrackedVacancy>,
        onUpdate: (List<TrackedVacancy>) -> Unit
    ) {
        val updatedList = currentList.map {
            if (it.jobInfo.jobUrl == vacancy.jobInfo.jobUrl) {
                it.copy(status = newStatus)
            } else {
                it
            }
        }
        onUpdate(updatedList)
    }

    private fun updateVacancyNotes(
        vacancy: TrackedVacancy,
        newNotes: String,
        currentList: List<TrackedVacancy>,
        onUpdate: (List<TrackedVacancy>) -> Unit
    ) {
        val updatedList = currentList.map {
            if (it.jobInfo.jobUrl == vacancy.jobInfo.jobUrl) {
                it.copy(notes = newNotes)
            } else {
                it
            }
        }
        onUpdate(updatedList)
    }

    @Composable
    fun EmptyTrackedVacanciesMessage() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(17,18,20,255)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "You haven't tracked any vacancies yet.\nGo to the Jobs page to start tracking!",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun VacancyRow(
        vacancy: TrackedVacancy,
        onStatusChange: (VacancyStatus) -> Unit,
        onNotesChange: (String) -> Unit,
        onDeleteClick: () -> Unit,
        onCalendarClick: () -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        var editingNotes by remember { mutableStateOf(false) }
        var notesText by remember { mutableStateOf(vacancy.notes) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Column 1: Vacancy & Company
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    vacancy.jobInfo.jobName,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(199,194,200)
                )
                Text(
                    "at ${vacancy.jobInfo.companyName}",
                    color = Color(199,194,200),
                    fontSize = 14.sp,
                )
                Text(
                    "View Details",
                    color = Color(0xFF864AED),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable {
                            window.open(vacancy.jobInfo.jobUrl, "_blank")
                        }
                )
            }

            // Column 2: Status dropdown
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                var buttonWidth by remember { mutableStateOf(0) }

                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            buttonWidth = coordinates.size.width
                        },
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = getStatusColor(vacancy.status).copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        vacancy.status.displayName,
                        color = getStatusColor(vacancy.status),
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select Status",
                        tint = getStatusColor(vacancy.status)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .width(with(LocalDensity.current) { buttonWidth.toDp() })
                        .background(Color(199,194,200))
                ) {
                    VacancyStatus.entries.forEach { status ->
                        DropdownMenuItem(
                            onClick = {
                                onStatusChange(status)
                                expanded = false
                            }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            getStatusColor(status),
                                            RoundedCornerShape(2.dp)
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(status.displayName)
                            }
                        }
                    }
                }
            }

            // Column 3: Notes
            Box(
                modifier = Modifier
                    .weight(2f)
                    .padding(start = 8.dp)
            ) {
                if (editingNotes) {
                    Column {
                        TextField(
                            value = notesText,
                            onValueChange = { notesText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color(17,18,20,255),
                                textColor = Color(199,194,200),
                                placeholderColor = Color(199,194,200)
                            ),
                            placeholder = {
                                Text("Add notes about this application...",
                                    color = Color(199,194,200))
                            }
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    editingNotes = false
                                    notesText = vacancy.notes
                                }
                            ) {
                                Text("Cancel")
                            }
                            TextButton(
                                onClick = {
                                    onNotesChange(notesText)
                                    editingNotes = false
                                }
                            ) {
                                Text("Save")
                            }
                        }
                    }
                } else {
                    Column(modifier = Modifier.clickable { editingNotes = true }) {
                        if (vacancy.notes.isBlank()) {
                            Text(
                                "Click to add notes...",
                                color = Color(199,194,200),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        } else {
                            Text(
                                vacancy.notes,
                                color = Color(199,194,200),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (vacancy.notes.length > 100) {
                                Text(
                                    "Click to edit...",
                                    color = MaterialTheme.colors.primary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Column 4: Actions (Three-dot menu)
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .padding(start = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                // Three-dot menu
                var showMenu by remember { mutableStateOf(false) }

                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = Color(199,194,200),
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(Color(30, 31, 34, 255))
                ) {
                    // Delete option
                    DropdownMenuItem(
                        onClick = {
                            onDeleteClick()
                            showMenu = false
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red.copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete", color = Color(199,194,200))
                        }
                    }

                    // Calendar option
                    DropdownMenuItem(
                        onClick = {
                            onCalendarClick()
                            showMenu = false
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "Calendar",
                                tint = Color(0xFF864AED),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Plan meeting", color = Color(199,194,200))
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun VacancyCard(
        vacancy: TrackedVacancy,
        offsetX: Float,
        onStatusChange: (VacancyStatus) -> Unit,
        onNotesChange: (String) -> Unit,
        onDrag: (Float) -> Unit,
        onDragEnd: () -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        var editingNotes by remember { mutableStateOf(false) }
        var notesText by remember { mutableStateOf(vacancy.notes) }
        val density = LocalDensity.current

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val cornerRadius = 8.dp
            val cardShape = RoundedCornerShape(cornerRadius)

            // Track card size
            var cardHeight by remember { mutableStateOf(0.dp) }
            var cardWidth by remember { mutableStateOf(0.dp) }

            // Delete background (visible when swiping)
            Box(
                modifier = Modifier
                    .width(cardWidth)
                    .height(cardHeight)
                    .clip(cardShape)
                    .background(Color.Red.copy(alpha = 0.8f)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    modifier = Modifier.padding(end = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Delete",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            // Card content
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        // Update size variables based on actual card size
                        cardHeight = with(density) { coordinates.size.height.toDp() }
                        cardWidth = with(density) { coordinates.size.width.toDp() }
                    }
                    .offset(with(density) { offsetX.toDp() }, 0.dp)
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            onDrag(delta)
                        },
                        onDragStopped = {
                            onDragEnd()
                        }
                    ),
                backgroundColor = Color(17, 18, 20, 255),
                shape = cardShape,
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    // First block: Vacancy info and link
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(30, 31, 34, 255),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            vacancy.jobInfo.jobName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(199, 194, 200)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "at ${vacancy.jobInfo.companyName}",
                            color = Color(199, 194, 200),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "View Details",
                            color = Color(0xFF864AED),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable {
                                    window.open(vacancy.jobInfo.jobUrl, "_blank")
                                }
                                .align(Alignment.End)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Second block: Status
                    var buttonWidth by remember { mutableStateOf(0) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    buttonWidth = coordinates.size.width
                                },
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = getStatusColor(vacancy.status).copy(alpha = 0.3f)
                            )
                        ) {
                            Text(
                                vacancy.status.displayName,
                                color = getStatusColor(vacancy.status),
                                modifier = Modifier.weight(1f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select Status",
                                tint = getStatusColor(vacancy.status)
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .width(with(LocalDensity.current) { buttonWidth.toDp() })
                                .background(Color(199, 194, 200))
                        ) {
                            VacancyStatus.entries.forEach { status ->
                                DropdownMenuItem(
                                    onClick = {
                                        onStatusChange(status)
                                        expanded = false
                                    }
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(
                                                    getStatusColor(status),
                                                    RoundedCornerShape(2.dp)
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(status.displayName)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Third block: Notes
                    if (editingNotes) {
                        TextField(
                            value = notesText,
                            onValueChange = { notesText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color(30, 31, 34, 255),
                                textColor = Color(199, 194, 200),
                                placeholderColor = Color(150, 150, 150)
                            ),
                            placeholder = {
                                Text("Add notes about this application...",
                                    color = Color(150, 150, 150))
                            }
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    editingNotes = false
                                    notesText = vacancy.notes
                                }
                            ) {
                                Text("Cancel")
                            }
                            TextButton(
                                onClick = {
                                    onNotesChange(notesText)
                                    editingNotes = false
                                }
                            ) {
                                Text("Save")
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(30, 31, 34, 255),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { editingNotes = true }
                                .padding(12.dp)
                        ) {
                            if (vacancy.notes.isBlank()) {
                                Text(
                                    "Tap to add notes...",
                                    color = Color(150, 150, 150),
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            } else {
                                Text(
                                    vacancy.notes,
                                    color = Color(199, 194, 200),
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun DeleteConfirmationDialog(
        vacancy: TrackedVacancy,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Delete Tracked Vacancy")
            },
            text = {
                Column {
                    Text("Are you sure you want to delete this vacancy from your tracking list?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = vacancy.jobInfo.jobName,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "at ${vacancy.jobInfo.companyName}",
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This action cannot be undone.",
                        color = Color.Red
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }

    private fun getStatusColor(status: VacancyStatus): Color {
        return when (status) {
            VacancyStatus.INTERESTED -> Color(0xFF2196F3)      // Blue
            VacancyStatus.RESUME_SENT -> Color(0xFFFF9800)     // Orange
            VacancyStatus.INTERVIEW_SCHEDULED -> Color(0xFF864AED) // Purple
            VacancyStatus.OFFER -> Color(0xFF4CAF50)           // Green
            VacancyStatus.REJECTED -> Color(0xFFF44336)        // Red
            VacancyStatus.NOT_INTERESTED -> Color(0xFF9E9E9E)  // Gray
        }
    }
}
