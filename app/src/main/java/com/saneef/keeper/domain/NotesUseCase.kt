package com.saneef.keeper.domain

import com.saneef.keeper.model.NoteUiModel
import kotlinx.coroutines.flow.Flow

interface NotesUseCase {

    fun allNotes(): Flow<List<NoteUiModel>>
    suspend fun insertNote(noteUiModel: NoteUiModel)
    suspend fun deleteNote(id: Long)
    suspend fun deleteNotes()
    suspend fun updateNote(noteUiModel: NoteUiModel)
    suspend fun exportNotes(): Boolean
    suspend fun importNotes(filename: String): Boolean
}
