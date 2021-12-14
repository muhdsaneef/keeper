package com.saneef.keeper.di

import android.app.Application
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

    @Provides
    @Singleton
    fun provideDatabase(application: Application): NotesRoomDatabase {
        return NotesRoomDatabase.buildDatabase(application)
    }

    @Provides
    @Singleton
    fun provideDao(database: NotesRoomDatabase): NotesDao = database.noteDao()
}
