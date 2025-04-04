package uk.ac.tees.mad.moodlog.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.moodlog.MainActivity
import uk.ac.tees.mad.moodlog.view.navigation.SubGraph
import uk.ac.tees.mad.moodlog.view.utils.ImageFileProvider
import uk.ac.tees.mad.moodlog.view.utils.LocationUtils
import uk.ac.tees.mad.moodlog.viewmodel.JournalScreenViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    navController: NavHostController, viewmodel: JournalScreenViewModel = koinViewModel()
) {
    val sliderPosition by viewmodel.sliderPosition.collectAsStateWithLifecycle()
    val mood by viewmodel.mood.collectAsStateWithLifecycle()
    val noteContent by viewmodel.journalContent.collectAsStateWithLifecycle()
    val databaseData by viewmodel.journalData.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val locationUtils = LocationUtils(context)
    val location = viewmodel.location.value
    val address = location?.let {
        locationUtils.reverseGeocodeLocation(location)
    }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(), onResult = { permissions ->
            if (permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true && permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                // I have access to location

                locationUtils.requestLocationUpdates(viewmodel = viewmodel)
            } else {
                // Ask for permission
                val rationalRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if (rationalRequired) {
                    Toast.makeText(context, "Location Permission Required", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        context,
                        "Location Permission Required, Please enable from settings",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

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

                    viewmodel.updateSelectedDate(datePickerState.selectedDateMillis.toString())
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

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Slider(
                            value = sliderPosition,
                            onValueChange = { viewmodel.updateSliderPosition(it) },
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
                            text = mood,
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

                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { viewmodel.updateJournalContent(it) },
                        placeholder = { Text("Write how you're feeling and why...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = MaterialTheme.shapes.extraLarge
                    )
                }

                // Add Photo
                item {
                    var hasImage by remember {
                        mutableStateOf(false)
                    }
                    var imageUri by remember {
                        mutableStateOf<Uri?>(null)
                    }
                    val context = LocalContext.current
                    val imagePicker = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent(), onResult = { uri ->
                            hasImage = uri != null
                            imageUri = uri
                        })
                    val cameraLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.TakePicture(), onResult = { success ->
                            hasImage = success
                        })
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
                            if (hasImage && imageUri != null) {
                                AsyncImage(
                                    model = imageUri,
                                    modifier = Modifier.fillMaxSize(),
                                    contentDescription = "Selected image",
                                )
                            } else {
                                // Photo placeholder
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = "Add Photo",
                                    modifier = Modifier.size(40.dp),
                                    tint = Color.Gray
                                )
                            }
                        }
                        Column {
                            Button(
                                onClick = {
                                    imagePicker.launch("image/*")
                                },
                            ) {
                                Text(text = "Select Image")
                            }
                            Button(
                                onClick = {
                                    hasImage = false
                                    imageUri = null
                                    val uri = ImageFileProvider.getImageUri(context)
                                    imageUri = uri
                                    cameraLauncher.launch(uri)
                                },
                            ) {
                                Text(text = "Take Photo")
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
                            .clickable {
                                if (locationUtils.hasLocationPermission(context)) {
                                    // Permission already granted, update the location
                                    locationUtils.requestLocationUpdates(viewmodel = viewmodel)
                                } else {
                                    //Request location permission
                                    requestPermissionLauncher.launch(
                                        arrayOf(
                                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                            android.Manifest.permission.ACCESS_FINE_LOCATION
                                        )
                                    )
                                }
                            }, shape = MaterialTheme.shapes.extraLarge
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
                            AnimatedVisibility(address == null) {
                                Text(
                                    text = "Add your location",
                                    color = Color.Gray,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            AnimatedVisibility(address != null) {
                                Text(
                                    text = "$address", modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Save Button
                item {
                    Button(
                        onClick = {
                            viewmodel.saveJournal()
                        },
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
                item{
                    Text(text = "Database Data")
                }
                items(databaseData, key = {it.id}) {data->
                    Column{
                        Text(text= data.id.toString())
                        Text(text = data.journalMood)
                        Text(text = data.journalTime)
                        Text(text = data.journalDate)
                        Text(text = data.journalContent)
                        Text(text = data.journalLocationAddress)
                        Text(text = data.journalLocationLatitude.toString())
                        Text(text = data.journalLocationLongitude.toString())
                        Text(text = data.journalImage)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}