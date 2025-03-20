package uk.ac.tees.mad.moodlog.di

import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uk.ac.tees.mad.moodlog.model.network.NetworkConnectivityManager
import uk.ac.tees.mad.moodlog.model.repository.AuthRepository
import uk.ac.tees.mad.moodlog.model.repository.NetworkRepository
import uk.ac.tees.mad.moodlog.viewmodel.AuthScreenViewModel
import uk.ac.tees.mad.moodlog.viewmodel.SplashScreenViewModel

val appModule = module {
    // Network
    single { NetworkConnectivityManager(androidContext()) }
    single { NetworkRepository(get()) }

    // Firebase
    single { FirebaseAuth.getInstance() }
    single { AuthRepository(get()) }

    // ViewModels
    viewModelOf(::SplashScreenViewModel)
    viewModelOf(::AuthScreenViewModel)
}