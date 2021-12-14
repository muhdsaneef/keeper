package com.saneef.keeper.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.saneef.keeper.model.DbNote
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Query("SELECT * from note_table ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<DbNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: DbNote)

    @Update
    fun update(note: DbNote)
}
