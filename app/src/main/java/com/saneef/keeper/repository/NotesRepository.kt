package com.saneef.keeper.repository

import android.util.Log
import com.saneef.keeper.TimestampHelper
import com.saneef.keeper.dao.NotesDao
import com.saneef.keeper.domain.NotesDbMapper
import com.saneef.keeper.model.DbNote
import com.saneef.keeper.model.NoteUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotesRepository @Inject constructor(
    private val notesDao: NotesDao,
    private val notesDbMapper: NotesDbMapper,
    private val timestampHelper: TimestampHelper
    ) {
    val notesFlow: Flow<List<NoteUiModel>>
        get() = notesDao.getAllNotes()
            .map { it.map { note -> NoteUiModel(id = note.id, title = note.title, description = note.description) } }

    fun insert(note: NoteUiModel) {
        notesDao.insert(notesDbMapper.map(note))
    }

    fun update(note: NoteUiModel) {
        val updatedNote = note.run {
            DbNote(id, title, timestampHelper.currentTimestamp, description)
        }
        notesDao.update(updatedNote)
    }

    fun delete(id: Long) {
        Log.d("ID", "$id")
        notesDao.delete(id)
    }

    fun exportAllNotes(): Boolean {
        return true
    }
}
