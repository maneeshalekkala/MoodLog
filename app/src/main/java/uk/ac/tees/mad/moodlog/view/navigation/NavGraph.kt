package uk.ac.tees.mad.moodlog.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.ac.tees.mad.moodlog.ui.screens.AuthScreen
import uk.ac.tees.mad.moodlog.ui.screens.HistoryScreen
import uk.ac.tees.mad.moodlog.ui.screens.JournalScreen
import uk.ac.tees.mad.moodlog.ui.screens.ProfileScreen
import uk.ac.tees.mad.moodlog.ui.screens.SplashScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController, startDestination = Dest.SplashScreen
    ) {
        composable<Dest.SplashScreen> {
            SplashScreen(navController = navController)
        }
        navigation<SubGraph.AuthGraph>(startDestination = Dest.AuthScreen) {
            composable<Dest.AuthScreen> {
                AuthScreen(navController = navController)
            }
        }
        navigation<SubGraph.HomeGraph>(startDestination = Dest.JournalScreen) {
            composable<Dest.JournalScreen> {
                JournalScreen(navController = navController)
            }
            composable<Dest.HistoryScreen> {
                HistoryScreen(navController = navController)
            }
            composable<Dest.ProfileScreen> {
                ProfileScreen(navController = navController)
            }
        }
    }
}