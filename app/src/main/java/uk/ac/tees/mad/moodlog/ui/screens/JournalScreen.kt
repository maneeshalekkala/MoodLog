package uk.ac.tees.mad.moodlog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.moodlog.view.navigation.SubGraph
import uk.ac.tees.mad.moodlog.viewmodel.JournalScreenViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    navController: NavHostController, viewmodel: JournalScreenViewModel = koinViewModel()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(), topBar = {
            TopAppBar(title = { Text(text = "Journal Screen") })
        }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp),
            ) {
                // Date Selector
                item {
                    val datePickerState = rememberDatePickerState()
                    var showDatePicker by remember { mutableStateOf(false) }

                    val selectedDate = datePickerState.selectedDateMillis?.let {
                        SimpleDateFormat("EEEE, d MMMM, yyyy", Locale.getDefault()).format(Date(it))
                    } ?: SimpleDateFormat("EEEE, d MMMM, yyyy", Locale.getDefault()).format(Date())

                    Text(
                        text = "Date",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Calendar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = selectedDate, modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Date"
                            )
                        }
                    }

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text("Cancel")
                                }
                            }) {
                            DatePicker(state = datePickerState)
                        }
                    }
                }
                // Mood Slider
                item {
                    Text(
                        text = "How are you feeling?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                    )

                    var sliderPosition by remember { mutableFloatStateOf(2f) }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Slider(
                            value = sliderPosition,
                            onValueChange = { sliderPosition = it },
                            valueRange = 0f..4f,
                            steps = 3
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Icon(
                                    imageVector = Icons.Filled.SentimentVeryDissatisfied,
                                    contentDescription = "Very Dissatisfied"
                                )
                            }
                            Column {
                                Icon(
                                    imageVector = Icons.Filled.SentimentVerySatisfied,
                                    contentDescription = "Very Satisfied"
                                )
                            }
                        }

                        Icon(
                            modifier = Modifier
                                .size(36.dp)
                                .align(Alignment.CenterHorizontally),
                            imageVector = when (sliderPosition) {
                                0f -> Icons.Filled.SentimentVeryDissatisfied
                                1f -> Icons.Filled.SentimentDissatisfied
                                2f -> Icons.Filled.SentimentNeutral
                                3f -> Icons.Filled.SentimentSatisfied
                                4f -> Icons.Filled.SentimentVerySatisfied
                                else -> Icons.Filled.Mood
                            },
                            contentDescription = "Mood"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = when (sliderPosition) {
                                0f -> "Very Dissatisfied"
                                1f -> "Dissatisfied"
                                2f -> "Neutral"
                                3f -> "Satisfied"
                                4f -> "Very Satisfied"
                                else -> "Mood"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                    }
                }

                // Notes Section
                item {
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                    )

                    var noteContent by remember { mutableStateOf("") }

                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        placeholder = { Text("Write how you're feeling and why...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = MaterialTheme.shapes.extraLarge
                    )
                }

                // Add Photo
                item {
                    Text(
                        text = "Add Photo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Photo placeholder
                        Box(
                            modifier = Modifier
                                .size(width = 200.dp, height = 200.dp)
                                .clip(MaterialTheme.shapes.extraLarge)
                                .background(Color.LightGray.copy(alpha = 0.3f))
                                .border(
                                    width = 1.dp,
                                    color = Color.Gray.copy(alpha = 0.5f),
                                    shape = MaterialTheme.shapes.extraLarge
                                ), contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Add Photo",
                                modifier = Modifier.size(40.dp),
                                tint = Color.Gray
                            )
                        }
                        Column {
                            Button(
                                onClick = { /*TODO*/ },
                            ) {
                                Text(text = "Capture Image")
                            }
                            Button(
                                onClick = { /*TODO*/ },
                            ) {
                                Text(text = "Select Image")
                            }
                        }

                    }
                }

                // Location Section
                item {
                    Text(
                        text = "Location",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* TODO: Open location picker */ },
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Add your location",
                                color = Color.Gray,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Save Button
                item {
                    Button(
                        onClick = { /* TODO: Save journal entry */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(top = 8.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save, contentDescription = "Save"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save Entry",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                item {
                    Button(
                        onClick = {
                            viewmodel.logOut()
                            navController.navigate(SubGraph.AuthGraph) {
                                popUpTo(SubGraph.HomeGraph) {
                                    inclusive = true
                                }
                            }
                        },
                    ) {
                        Text(text = "Log Out")
                    }
                }
            }
        }
    }
}