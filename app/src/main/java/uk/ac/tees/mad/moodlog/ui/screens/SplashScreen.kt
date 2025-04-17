package uk.ac.tees.mad.moodlog.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.moodlog.R
import uk.ac.tees.mad.moodlog.model.dataclass.state.LoadingState
import uk.ac.tees.mad.moodlog.model.firestore.JournalSynchronizer
import uk.ac.tees.mad.moodlog.view.navigation.Dest
import uk.ac.tees.mad.moodlog.view.navigation.SubGraph
import uk.ac.tees.mad.moodlog.viewmodel.SplashScreenViewModel
import uk.ac.tees.mad.sn.view.utils.LoadingErrorScreen

@Composable
fun SplashScreen(
    navController: NavHostController,
    journalSynchronizer: JournalSynchronizer,
    viewmodel: SplashScreenViewModel = koinViewModel()
) {
    val loadingState by viewmodel.loadingState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    var permissionsRequested by remember { mutableStateOf(false) }

    val permissionsToRequest = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(), onResult = { permissions ->
            val allPermissionsGranted = permissions.all { it.value }
            if (allPermissionsGranted) {
                viewmodel.onPermissionsGranted()
            } else {
                val rationalRequired = permissionsToRequest.any { permission ->
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        context as android.app.Activity, permission
                    )
                }
                if (rationalRequired) {
                    showPermissionDialog = true
                } else {
                    Toast.makeText(
                        context,
                        "Permission Required, Please enable from settings",
                        Toast.LENGTH_SHORT
                    ).show()
                    viewmodel.onPermissionsDenied()
                }
            }
        })

    LaunchedEffect(key1 = loadingState) {
        if (loadingState is LoadingState.Loading && !permissionsRequested) {
            val allPermissionsGranted = permissionsToRequest.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
            if (!allPermissionsGranted) {
                requestPermissionLauncher.launch(permissionsToRequest)
                permissionsRequested = true
            } else {
                viewmodel.onPermissionsGranted()
            }
        }
    }

    if (showPermissionDialog) {
        AlertDialog(onDismissRequest = { showPermissionDialog = false }, title = {
            Text(text = "Permissions Required")
        }, text = {
            Text(text = "This app needs permissions to function properly. Please grant them.")
        }, confirmButton = {
            Button(onClick = {
                requestPermissionLauncher.launch(permissionsToRequest)
                showPermissionDialog = false
            }) {
                Text(text = "Grant Permissions")
            }
        }, dismissButton = {
            Button(onClick = {
                showPermissionDialog = false
                viewmodel.onPermissionsDenied()
            }) {
                Text(text = "Deny")
            }
        })
    }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(
                targetState = loadingState,
                animationSpec = tween(durationMillis = 1000),
                label = "splashScreen"
            ) { state ->
                when (state) {
                    is LoadingState.Loading -> {
                        Splash()
                    }

                    is LoadingState.Success -> {
                        LaunchedEffect(key1 = Unit) {
                            if (viewmodel.isSignedIn()) {
                                journalSynchronizer.startSync()
                                navController.navigate(SubGraph.HomeGraph) {
                                    popUpTo(Dest.SplashScreen) {
                                        inclusive = true
                                    }
                                }
                            } else {
                                navController.navigate(SubGraph.AuthGraph) {
                                    popUpTo(Dest.SplashScreen) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }

                    is LoadingState.Error -> {
                        LoadingErrorScreen(
                            errorMessage = state.message, onRetry = {
                                viewmodel.startLoading()
                                permissionsRequested = false
                                showPermissionDialog = false
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun Splash() {
    val logoScale = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val tagLineAndProgressbarAlpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        // Logo animation
        logoScale.animateTo(
            targetValue = 1f, animationSpec = tween(durationMillis = 1000)
        )
        // App name animation
        delay(500) // Stagger the animation
        textAlpha.animateTo(
            targetValue = 1f, animationSpec = tween(durationMillis = 1000)
        )
        // Tagline animation
        delay(500) // Stagger the animation
        tagLineAndProgressbarAlpha.animateTo(
            targetValue = 1f, animationSpec = tween(durationMillis = 1000)
        )

    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .size(150.dp)
                .scale(logoScale.value),
            painter = painterResource(id = R.drawable.moodlog_logo),
            contentDescription = "App Logo",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
        Text(
            text = "MoodLog",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 20.dp)
                .alpha(textAlpha.value)
        )
        LinearProgressIndicator(
            modifier = Modifier
                .padding(top = 20.dp)
                .alpha(tagLineAndProgressbarAlpha.value),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Track your emotional journey",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(top = 10.dp)
                .alpha(tagLineAndProgressbarAlpha.value)
        )
    }
}