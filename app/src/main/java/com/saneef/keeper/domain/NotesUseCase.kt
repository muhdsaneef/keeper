package com.saneef.keeper.domain

import com.saneef.keeper.model.NoteUiModel
import kotlinx.coroutines.flow.Flow

interface NotesUseCase {

    suspend fun fetchNotes(): Flow<List<NoteUiModel>>
    suspend fun insertNote(noteUiModel: NoteUiModel)
    fun deleteNote()
    fun deleteNotes()
    suspend fun updateNote(noteUiModel: NoteUiModel)
}
