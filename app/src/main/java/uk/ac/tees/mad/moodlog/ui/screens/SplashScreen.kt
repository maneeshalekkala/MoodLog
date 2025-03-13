package uk.ac.tees.mad.moodlog.ui.screens

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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.moodlog.R
import uk.ac.tees.mad.moodlog.model.dataclass.state.LoadingState
import uk.ac.tees.mad.moodlog.view.navigation.Dest
import uk.ac.tees.mad.moodlog.view.navigation.SubGraph
import uk.ac.tees.mad.moodlog.viewmodel.SplashScreenViewModel
import uk.ac.tees.mad.sn.view.utils.LoadingErrorScreen

@Composable
fun SplashScreen(
    navController: NavHostController, viewmodel: SplashScreenViewModel = koinViewModel()
) {
    val loadingState by viewmodel.loadingState.collectAsStateWithLifecycle()
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
                        navController.navigate(SubGraph.AuthGraph) {
                            popUpTo(Dest.SplashScreen) {
                                inclusive = true
                            }
                        }
                    }

                    is LoadingState.Error -> {
                        LoadingErrorScreen(
                            errorMessage = state.message, onRetry = { viewmodel.startLoading() })
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