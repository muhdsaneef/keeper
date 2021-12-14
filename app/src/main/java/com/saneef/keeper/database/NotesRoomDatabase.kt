package com.saneef.keeper.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.saneef.keeper.dao.NotesDao
import com.saneef.keeper.model.DbNote

@Database(entities = [DbNote::class], version = 1)
abstract class NotesRoomDatabase : RoomDatabase() {
    abstract fun noteDao(): NotesDao

    companion object {
        private const val DB_NAME = "Notes_database"

        fun buildDatabase(application: Application) = Room.databaseBuilder(
            application,
            NotesRoomDatabase::class.java,
            DB_NAME
        ).fallbackToDestructiveMigration().build()
    }
}
