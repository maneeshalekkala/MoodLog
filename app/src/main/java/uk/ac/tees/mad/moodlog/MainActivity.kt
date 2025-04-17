package uk.ac.tees.mad.moodlog

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import org.koin.android.ext.android.inject
import uk.ac.tees.mad.moodlog.model.firestore.JournalSynchronizer
import uk.ac.tees.mad.moodlog.ui.screens.LocalIsDarkMode
import uk.ac.tees.mad.moodlog.ui.theme.MoodLogTheme
import uk.ac.tees.mad.moodlog.view.navigation.SetupNavGraph

class MainActivity : ComponentActivity() {
    val journalSynchronizer: JournalSynchronizer by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
//            setKeepOnScreenCondition {
//                // Condition to check if the app is still loading and showing the splash screen or not.
//                TODO()
//                TODO(Check Permissions granted or not (Camera, Storage, Location))
//            }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView, View.SCALE_X, 0.4f, 0.0f
                )
                zoomX.interpolator = OvershootInterpolator()
                zoomX.duration = 500L
                zoomX.doOnEnd { screen.remove() }
                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView, View.SCALE_Y, 0.4f, 0.0f
                )
                zoomY.interpolator = OvershootInterpolator()
                zoomY.duration = 500L
                zoomY.doOnEnd { screen.remove() }

                zoomX.start()
                zoomY.start()
            }
        }
        enableEdgeToEdge()
        setContent {
            val sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE)
            val isDarkMode = remember {
                mutableStateOf(sharedPreferences.getBoolean("dark_mode", true))
            }
            CompositionLocalProvider(LocalIsDarkMode provides isDarkMode) {
                MoodLogTheme(darkTheme = isDarkMode.value) {
                    val navController = rememberNavController()
                    SetupNavGraph(
                        navController = navController, journalSynchronizer = journalSynchronizer
                    )
                }
            }
        }
    }
}