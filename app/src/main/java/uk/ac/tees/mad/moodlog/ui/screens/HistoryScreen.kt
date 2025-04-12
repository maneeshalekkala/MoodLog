package uk.ac.tees.mad.moodlog.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.moodlog.R
import uk.ac.tees.mad.moodlog.model.dataclass.room.LocalJournalData
import uk.ac.tees.mad.moodlog.view.navigation.Dest
import uk.ac.tees.mad.moodlog.viewmodel.HistoryScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    historyScreenViewModel: HistoryScreenViewModel = koinViewModel()
) {
    val journalData by historyScreenViewModel.journalData.collectAsStateWithLifecycle()
    var searchText by remember { mutableStateOf("") }
    var isFilterExpanded by remember { mutableStateOf(false) }
    // Mock data for demonstration
    val mockJournalEntries = listOf(
        LocalJournalData(
            id = 1,
            journalDate = "2024-03-15",
            journalTime = "10:00",
            journalMood = "Happy",
            journalContent = "Had a great day!",
            journalLocationAddress = "London",
            journalImage = "image_url_1",
        ),
        LocalJournalData(
            id = 2,
            journalDate = "2024-03-16",
            journalTime = "14:30",
            journalMood = "Neutral",
            journalContent = "Just an ordinary day.",
            journalLocationAddress = "Manchester",
            journalImage = "image_url_2",
        ),
        LocalJournalData(
            id = 3,
            journalDate = "2024-03-17",
            journalTime = "18:45",
            journalMood = "Sad",
            journalContent = "Feeling a bit down.",
            journalLocationAddress = "Birmingham",
            journalImage = "image_url_3",
        ),
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("History") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Dest.JournalScreen)
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Journal Entry")
            }
        }
    ) { innerPadding ->
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
                    modifier = Modifier.clickable { isFilterExpanded = !isFilterExpanded }
                )
            }

            if (isFilterExpanded) {
                // Display filter UI components here
                Text("Filter UI would go here. (Date range, Mood selection)", modifier = Modifier.padding(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of journal entries
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(journalData) { entry ->
                    JournalEntryItem(entry = entry)
                }
            }
        }
    }
}

@Composable
fun JournalEntryItem(entry: LocalJournalData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${entry.journalDate} ${entry.journalTime}", fontWeight = FontWeight.Bold)
            }
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

        }
    }
}