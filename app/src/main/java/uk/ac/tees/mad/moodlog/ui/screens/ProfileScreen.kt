package uk.ac.tees.mad.moodlog.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.moodlog.model.dataclass.firebase.AuthResult
import uk.ac.tees.mad.moodlog.view.navigation.SubGraph
import uk.ac.tees.mad.moodlog.viewmodel.ProfileScreenViewModel

val LocalIsDarkMode = staticCompositionLocalOf { mutableStateOf(true) }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewmodel: ProfileScreenViewModel = koinViewModel(),
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val offlineMode by viewmodel.offlineMode.collectAsStateWithLifecycle()

    val userData by viewmodel.userData.collectAsStateWithLifecycle()
    val userDetailsResult by viewmodel.userDetails.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    val isDarkMode = LocalIsDarkMode.current

    var editNameState by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(userData.userDetails?.displayName ?: "") }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text(
                        text = "Profile",
                        maxLines = 1,
                        fontSize = 30.sp,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }, navigationIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(36.dp),
                        )
                    }
                }
            }, actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    AnimatedVisibility(offlineMode == true) {
                        Icon(
                            Icons.Outlined.CloudOff,
                            "Offline",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    AnimatedVisibility(offlineMode == false) {
                        Icon(Icons.Outlined.CloudDone, "Online", tint = Color.Green)
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                }
            }, scrollBehavior = scrollBehavior
            )
        }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Personal Information",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                            Text(
                                text = "Personal Information",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp), thickness = 2.dp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            when (userDetailsResult) {
                                is AuthResult.Error -> {
                                    Column {
                                        Text(
                                            text = "Name",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Text(text = "Error fetching user name")
                                    }
                                    IconButton(onClick = {
                                        viewmodel.fetchUserDetails()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Replay,
                                            contentDescription = "Refresh",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                is AuthResult.Loading -> {
                                    Column {
                                        Text(
                                            text = "Name",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Text(text = "Loading...")
                                    }
                                    CircularProgressIndicator()
                                }

                                is AuthResult.Success -> {
                                    Column {
                                        if (editNameState == false) {
                                            Text(
                                                text = "Name",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            )
                                            Text(
                                                text = name,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        } else {
                                            OutlinedTextField(
                                                value = name,
                                                onValueChange = {
                                                    name = it
                                                },
                                                label = { Text("Name") },
                                                singleLine = true,
                                                maxLines = 1,
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Text,
                                                    imeAction = ImeAction.Done
                                                ),
                                                keyboardActions = KeyboardActions(onDone = {
                                                    if (name.isNotEmpty() && name.isNotBlank()) {
                                                        editNameState = !editNameState
                                                        viewmodel.updateDisplayName(name)
                                                    }
                                                })
                                            )
                                        }
                                    }
                                    IconButton(onClick = {
                                        viewmodel.updateDisplayName(name)
                                        editNameState = !editNameState
                                    }, enabled = name.isNotEmpty() && name.isNotBlank()) {
                                        if (editNameState == false) {
                                            if (name.isEmpty()) name = "Not Set"
                                            Icon(
                                                imageVector = Icons.Filled.Edit,
                                                contentDescription = "Edit Name",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        } else {
                                            if (name == "Not Set") name = ""
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = "Save Name",
                                                tint = if (name.isNotEmpty() && name.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
                                            )
                                        }
                                    }

                                }
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = "Email", fontWeight = FontWeight.Bold, fontSize = 18.sp
                        )
                        Text(
                            text = userData.userDetails?.email ?: "Loading...",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "App Settings",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                            Text(
                                text = "App Settings",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp), thickness = 2.dp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Theme", fontWeight = FontWeight.Bold, fontSize = 18.sp
                                )
                                Text(
                                    text = "Dark Mode", color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Switch(
                                checked = isDarkMode.value,
                                onCheckedChange = {
                                    isDarkMode.value = it
                                    sharedPreferences.edit().putBoolean("dark_mode", it).apply()
                                })
                        }
                    }
                }
            }
            item {
                // Logout
                Button(
                    onClick = {
                        /* Handle logout */
                        viewmodel.logOut()
                        navController.navigate(SubGraph.AuthGraph) {
                            popUpTo(SubGraph.HomeGraph) {
                                inclusive = true
                            }
                        }
                    }, modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Logout", fontSize = 24.sp, fontWeight = FontWeight.Bold
                        )
                    }

                }
            }
            item {
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}