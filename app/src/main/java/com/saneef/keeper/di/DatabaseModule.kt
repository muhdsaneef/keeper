package com.saneef.keeper.di

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.saneef.keeper.dao.NotesDao
import com.saneef.keeper.database.NotesRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val FIREBASE_DB_URL = "https://keeper-7b24a-default-rtdb.asia-southeast1.firebasedatabase.app/"

    @Provides
    @Singleton
    fun provideDatabase(application: Application): NotesRoomDatabase {
        return NotesRoomDatabase.buildDatabase(application)
    }

    @Provides
    @Singleton
    fun provideDao(database: NotesRoomDatabase): NotesDao = database.noteDao()

    @Provides
    @Singleton
    fun provideFirebaseDatabaseReference(): FirebaseDatabase = Firebase.database(FIREBASE_DB_URL)
}
