package uk.ac.tees.mad.moodlog.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.setSelection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.moodlog.R
import uk.ac.tees.mad.moodlog.model.dataclass.room.LocalJournalData
import uk.ac.tees.mad.moodlog.view.navigation.Dest
import uk.ac.tees.mad.moodlog.viewmodel.HistoryScreenViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    historyScreenViewModel: HistoryScreenViewModel = koinViewModel()
) {
    LaunchedEffect(key1 = Unit) {
        historyScreenViewModel.startLoadingJournalData()
    }
    val journalData by historyScreenViewModel.journalData.collectAsStateWithLifecycle()
    var searchText by remember { mutableStateOf("") }
    var isFilterExpanded by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf<String?>(null) }
    var isMoodDropdownExpanded by remember { mutableStateOf(false) }
    val moodOptions = listOf("Very Dissatisfied", "Dissatisfied", "Neutral", "Satisfied", "Very Satisfied")
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Filtered data
    val filteredData = remember(journalData, searchText, selectedMood, datePickerState.selectedDateMillis) {
        val selectedDateMillis = datePickerState.selectedDateMillis
        journalData.filter { entry ->
            val matchesSearchText =
                searchText.isBlank() || entry.journalContent.contains(searchText, ignoreCase = true)
            val matchesMood = selectedMood == null || entry.journalMood == selectedMood
            val matchesDate = if (selectedDateMillis != null) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = selectedDateMillis
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfDay = calendar.timeInMillis
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                val endOfDay = calendar.timeInMillis - 1
                entry.journalDate.toLong() in startOfDay..endOfDay
            } else {
                true
            }
            matchesSearchText && matchesMood && matchesDate
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = { Text("History") }, actions = {
            IconButton(
                onClick = {
                    historyScreenViewModel.startLoadingJournalData()
                    searchText = "" // Clear search text
                    selectedMood = null // Clear selected mood
                    datePickerState.selectedDateMillis = null // Clear selected date
                }) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
            }
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(
                onClick = {
                    navController.navigate(Dest.ProfileScreen)
                }) {
                Icon(Icons.Filled.AccountCircle, contentDescription = "Profile Screen")
            }
        })
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = {
                navController.navigate(Dest.JournalScreen)
            }) {
            Icon(Icons.Filled.Add, contentDescription = "Add Journal Entry")
        }
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                label = { Text("Search") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Filter section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isFilterExpanded = !isFilterExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Filter")
                    Text("Filter by Date/Mood", fontWeight = FontWeight.Bold)
                }
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand Filter",
                    modifier = Modifier.clickable { isFilterExpanded = !isFilterExpanded })
            }

            if (isFilterExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                // Mood Filter
                ExposedDropdownMenuBox(
                    expanded = isMoodDropdownExpanded,
                    onExpandedChange = { isMoodDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedMood ?: "Select Mood",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = isMoodDropdownExpanded
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isMoodDropdownExpanded,
                        onDismissRequest = { isMoodDropdownExpanded = false }
                    ) {
                        moodOptions.forEach { mood ->
                            DropdownMenuItem(
                                text = { Text(mood) },
                                onClick = {
                                    selectedMood = mood
                                    isMoodDropdownExpanded = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Clear") },
                            onClick = {
                                selectedMood = null
                                isMoodDropdownExpanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showDatePickerDialog = true }) {
                    Text("Select Date")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (showDatePickerDialog) {
                DatePickerDialog(
                    onDismissRequest = { showDatePickerDialog = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDatePickerDialog = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDatePickerDialog = false
                                datePickerState.selectedDateMillis = null // Clear date selection
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of journal entries
            LazyColumn(
                modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(filteredData) { entry ->
                    JournalEntryItem(entry = entry, viewModel = historyScreenViewModel)
                }
            }
        }
    }
}

@Composable
fun JournalEntryItem(entry: LocalJournalData, viewModel: HistoryScreenViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            val formattedDate = formatDateFromTimestamp(entry.journalDate.toLong())
            val formattedTime = formatTimeFromTimestamp(entry.journalTime.toLong())
            Text(text = "$formattedDate", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = entry.journalMood)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = entry.journalContent)
            Spacer(modifier = Modifier.height(8.dp))
            entry.journalLocationAddress?.let { Text(text = "Location: $it") }

            // Displaying image, only if not null
            if (entry.journalImage.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground), //TODO replace with actual image
                    contentDescription = "Journal Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Posted on: $formattedTime")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                viewModel.deleteJournalData(entry)
            }) {
                Text(text = "Delete")
            }
        }
    }
}

fun formatDateFromTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("d MMMM, yyyy", Locale.getDefault())
    return format.format(date)
}

fun formatTimeFromTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("HH:mm, d MM yyyy", Locale.getDefault())
    return format.format(date)
}