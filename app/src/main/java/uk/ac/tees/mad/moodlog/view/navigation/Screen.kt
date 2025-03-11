package uk.ac.tees.mad.moodlog.view.navigation

import kotlinx.serialization.Serializable

sealed class SubGraph {
    @Serializable
    data object AuthGraph : SubGraph()

    @Serializable
    data object HomeGraph : SubGraph()
}

sealed class Dest {
    @Serializable
    data object SplashScreen : Dest()

    @Serializable
    data object AuthScreen : Dest()

    @Serializable
    data object JournalScreen : Dest()

    @Serializable
    data object HistoryScreen : Dest()

    @Serializable
    data object ProfileScreen : Dest()
}