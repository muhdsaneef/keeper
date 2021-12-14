package com.saneef.keeper.domain

import com.saneef.keeper.model.NoteUiModel
import com.saneef.keeper.repository.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NotesUseCaseImpl(private val notesRepository: NotesRepository): NotesUseCase {

    override suspend fun fetchNotes(): Flow<List<NoteUiModel>> = withContext(Dispatchers.IO) {
        notesRepository.notesFlow
    }

    override suspend fun insertNote(noteUiModel: NoteUiModel) = withContext(Dispatchers.IO) {
        notesRepository.insert(noteUiModel)
    }

    override fun deleteNote() {

    }

    override fun deleteNotes() {

    }

    override suspend fun updateNote(noteUiModel: NoteUiModel) = withContext(Dispatchers.IO) {
        notesRepository.update(noteUiModel)
    }
}
