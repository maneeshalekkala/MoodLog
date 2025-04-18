package uk.ac.tees.mad.moodlog.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.moodlog.R
import uk.ac.tees.mad.moodlog.model.dataclass.room.LocalJournalData
import uk.ac.tees.mad.moodlog.view.navigation.Dest
import uk.ac.tees.mad.moodlog.viewmodel.HistoryScreenViewModel
import java.io.File
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
    val moodOptions =
        listOf("Very Dissatisfied", "Dissatisfied", "Neutral", "Satisfied", "Very Satisfied")
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Filtered data
    val filteredData =
        remember(journalData, searchText, selectedMood, datePickerState.selectedDateMillis) {
            val selectedDateMillis = datePickerState.selectedDateMillis
            journalData.filter { entry ->
                val matchesSearchText = searchText.isBlank() || entry.journalContent.contains(
                    searchText, ignoreCase = true
                )
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

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val focusManager = LocalFocusManager.current

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                //Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Image(
                    painter = painterResource(id = R.drawable.moodlog_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(36.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text(
                    text = "MoodLog",
                    maxLines = 1,
                    fontSize = 30.sp,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }, actions = {
            IconButton(
                onClick = {
                    historyScreenViewModel.startLoadingJournalData()
                    searchText = "" // Clear search text
                    selectedMood = null // Clear selected mood
                    datePickerState.selectedDateMillis = null // Clear selected date
                }) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = { navController.navigate(Dest.ProfileScreen) }) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile Screen Icon",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }, scrollBehavior = scrollBehavior
        )
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
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                label = { Text("Search Content") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                }),
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
                    Spacer(modifier = Modifier.width(8.dp))
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
                    onExpandedChange = { isMoodDropdownExpanded = it }) {
                    OutlinedTextField(
                        value = selectedMood ?: "Select Mood",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = isMoodDropdownExpanded
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isMoodDropdownExpanded,
                        onDismissRequest = { isMoodDropdownExpanded = false }) {
                        moodOptions.forEach { mood ->
                            DropdownMenuItem(text = { Text(mood) }, onClick = {
                                selectedMood = mood
                                isMoodDropdownExpanded = false
                            })
                        }
                        DropdownMenuItem(text = { Text("Clear") }, onClick = {
                            selectedMood = null
                            isMoodDropdownExpanded = false
                        })
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { showDatePickerDialog = true }) {
                        Text("Select Date")
                    }
                    if (datePickerState.selectedDateMillis != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Selected Date")
                            Text(text = formatDateFromTimestamp(datePickerState.selectedDateMillis!!))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                datePickerState.selectedDateMillis = null // Clear selected date
                            }) { Text("Reset Date") }
                    }
                }
            }

            if (showDatePickerDialog) {
                DatePickerDialog(
                    onDismissRequest = { showDatePickerDialog = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDatePickerDialog = false
                            }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDatePickerDialog = false
                                datePickerState.selectedDateMillis = null // Clear date selection
                            }) {
                            Text("Cancel")
                        }
                    }) {
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
            Text(text = "Mood: ${entry.journalMood}")
            Spacer(modifier = Modifier.height(8.dp))
            if (entry.journalContent.isNotEmpty() || (entry.journalContent.isNotBlank())) {
                Text(text = entry.journalContent)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (entry.journalLocationAddress == "null" || entry.journalLocationAddress == "") {
                Text(text = "Location: Not available")
            } else {
                Text(text = "Location: ${entry.journalLocationAddress}")
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Displaying image, only if not null
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(color = MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                fun decodeBase64ToBitmap(base64String: String): Bitmap? {
                    return try {
                        val decodedByteArray = Base64.decode(base64String, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(
                            decodedByteArray,
                            0,
                            decodedByteArray.size
                        )
                    } catch (e: Exception) {
                        // Handle decoding errors (e.g., invalid Base64 string)
                        null
                    }
                }
                val bitmap = if(entry.journalImage.isNotEmpty()){
                    decodeBase64ToBitmap(entry.journalImage)
                }else{
                    null
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = " Journal Image",
                    )
                } else {
                    // Photo placeholder
                    Image(
                        painter = painterResource(id = R.drawable.moodlog_logo),
                        contentDescription = "Journal Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
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

@Composable
fun ImageFromPath(imagePath: String) {
    val context = LocalContext.current
    val imageBitmap: ImageBitmap? = remember {
        val file = File(imagePath)

        if (file.exists()) {
            // Decode the file to Bitmap
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            //Convert to imageBitmap
            bitmap?.asImageBitmap()
        } else {
            null
        }
    }

    imageBitmap?.let {
        Image(
            bitmap = it,
            contentDescription = "Journal Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Fit
        )
    }
}