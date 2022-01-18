package com.saneef.keeper.domain

import com.saneef.keeper.model.NoteUiModel
import com.saneef.keeper.repository.NotesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NotesUseCaseImpl(
    private val notesRepository: NotesRepository,
    private val ioDispatcher: CoroutineDispatcher
) : NotesUseCase {

    override suspend fun fetchNotes(): Flow<List<NoteUiModel>> = withContext(ioDispatcher) {
        notesRepository.notesFlow
    }

    override suspend fun insertNote(noteUiModel: NoteUiModel) = withContext(ioDispatcher) {
        notesRepository.insert(noteUiModel)
    }

    override suspend fun deleteNote(id: Long) = withContext(ioDispatcher) {
        notesRepository.delete(id)
    }

    override suspend fun deleteNotes() {
        //  TODO: Implement all delete notes functionality
    }

    override suspend fun exportNotes(): Boolean = withContext(ioDispatcher) {
        notesRepository.exportAllNotes()
    }

    override suspend fun importNotes(filename: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateNote(noteUiModel: NoteUiModel) = withContext(ioDispatcher) {
        notesRepository.update(noteUiModel)
    }
}
