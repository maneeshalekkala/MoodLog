package uk.ac.tees.mad.moodlog.di

import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uk.ac.tees.mad.moodlog.model.firestore.JournalSynchronizer
import uk.ac.tees.mad.moodlog.model.network.NetworkConnectivityManager
import uk.ac.tees.mad.moodlog.model.repository.AuthRepository
import uk.ac.tees.mad.moodlog.model.repository.JournalFirestoreRepository
import uk.ac.tees.mad.moodlog.model.repository.LocalJournalDataRepository
import uk.ac.tees.mad.moodlog.model.repository.NetworkRepository
import uk.ac.tees.mad.moodlog.model.room.LocalJournalDataDatabase
import uk.ac.tees.mad.moodlog.viewmodel.AuthScreenViewModel
import uk.ac.tees.mad.moodlog.viewmodel.HistoryScreenViewModel
import uk.ac.tees.mad.moodlog.viewmodel.JournalScreenViewModel
import uk.ac.tees.mad.moodlog.viewmodel.ProfileScreenViewModel
import uk.ac.tees.mad.moodlog.viewmodel.SplashScreenViewModel

val appModule = module {
    // Network
    single { NetworkConnectivityManager(androidContext()) }
    single { NetworkRepository(get()) }

    // Firebase
    single { FirebaseAuth.getInstance() }
    single { AuthRepository(get()) }
    single { FirebaseFirestore.getInstance() }
    single { JournalFirestoreRepository(get()) }

    // Local Journal Data Database
    single {
        Room.databaseBuilder(
            androidApplication(),
            LocalJournalDataDatabase::class.java,
            "local_journal_data_database"
        ).build()
    }
    single {
        val database = get<LocalJournalDataDatabase>()
        database.localJournalDataDao()
    }
    single { LocalJournalDataRepository(get()) }

    //Journal synchronizer
    single { JournalSynchronizer(get(), get(), get()) }

    // ViewModels
    viewModelOf(::SplashScreenViewModel)
    viewModelOf(::AuthScreenViewModel)
    viewModelOf(::JournalScreenViewModel)
    viewModelOf(::HistoryScreenViewModel)
    viewModelOf(::ProfileScreenViewModel)
}