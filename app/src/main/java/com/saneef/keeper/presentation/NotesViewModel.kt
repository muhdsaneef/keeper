package com.saneef.keeper.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saneef.keeper.di.DefaultDispatcher
import com.saneef.keeper.domain.NotesUseCase
import com.saneef.keeper.model.NoteUiModel
import com.saneef.keeper.model.TodoItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesUseCase: NotesUseCase,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val notesState: MutableStateFlow<List<NoteUiModel>> = MutableStateFlow(emptyList())

    private val searchResultsState: MutableStateFlow<List<NoteUiModel>> = MutableStateFlow(emptyList())

    val notes: Flow<List<NoteUiModel>> = combine(notesState, searchResultsState) { notes, searchResults ->
        searchResults.ifEmpty {
            notes
        }
    }

    private val noteEventsChannel: Channel<NoteEvents> =
        Channel(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val noteEventsViewState: Flow<NoteEvents>
        get() = noteEventsChannel.receiveAsFlow()

    private val notesVisibilityState = MutableStateFlow(false)
    val notesVisibilityViewState: StateFlow<Boolean>
        get() = notesVisibilityState.asStateFlow()

    private var isBiometricVerified: Boolean = false

    private var isEditMode: Boolean = false
    private var noteId: Long = 0L

    init {
        viewModelScope.launch { notesUseCase.uploadTimestamp() }
        fetchAllNotes()
    }

    private fun fetchAllNotes() {
        viewModelScope.launch {
            notesUseCase.allNotes().collect {
                notesState.value = it
            }
        }
    }

    fun addNote(title: String, description: String, todoList: List<TodoItemUiModel>) {
        if (title.isEmpty() || description.isEmpty()) return

        viewModelScope.launch {
            runCatching {
                if (isEditMode) {
                    notesUseCase.updateNote(
                        NoteUiModel(
                            id = noteId,
                            title = title,
                            description = description,
                            todoList = todoList,
                        )
                    )
                } else {
                    notesUseCase.insertNote(
                        NoteUiModel(
                            title = title,
                            description = description,
                            todoList = todoList,
                        )
                    )
                }
                isEditMode = false
                noteEventsChannel.send(NoteEvents.BuilderUpdated(true))
            }.onFailure {
                noteEventsChannel.send(NoteEvents.BuilderUpdated(false))
            }
        }
    }

    fun searchNote(query: String) {
        if (query.isEmpty()) {
            searchResultsState.value = emptyList()
        } else {
            viewModelScope.launch {
                withContext(defaultDispatcher) {
                    notesState.value.let {
                        val searchResults = it.filter { note ->
                            note.title.contains(query, ignoreCase = true) ||
                                note.description.contains(query, ignoreCase = true)
                        }

                        searchResultsState.value = searchResults
                    }
                }
            }
        }
    }

    fun deleteNote(noteId: Long?) {
        viewModelScope.launch {
            if (noteId != null && notesState.value.any { it.id == noteId }) {
                notesUseCase.deleteNote(noteId)
                searchResultsState.value = emptyList()
            } else {
                Log.d("Failed to delete ID", "$noteId")
            }

            noteEventsChannel.send(NoteEvents.BuilderUpdated(true))
        }
    }

    fun toggleNotesVisibility() {
        if (isBiometricVerified.not()) {
            initiateBiometric()
        } else {
            notesVisibilityState.value = notesVisibilityState.value.not()
        }
    }

    private fun initiateBiometric() {
        viewModelScope.launch {
            noteEventsChannel.send(NoteEvents.BioMetricRequired)
        }
    }

    fun onCreateNotesClicked() {
        viewModelScope.launch {
            noteEventsChannel.send(NoteEvents.StartBuilder)
        }
    }

    fun onBiometricAuthenticationSucceeded() {
        isBiometricVerified = true
        notesVisibilityState.value = true
    }

    fun onEditClicked(noteUiModel: NoteUiModel) {
        viewModelScope.launch {
            if (isBiometricVerified) {
                noteEventsChannel.send(NoteEvents.EditRequired(noteUiModel))
            } else {
                initiateBiometric()
            }
        }
    }

    fun onNoteBuilderCreated(noteUiModel: NoteUiModel?) {
        if (noteUiModel != null) {
            isEditMode = true
            noteId = noteUiModel.id
        }
    }
}

sealed class NoteEvents {
    object NoSearchResults: NoteEvents()
    data class BuilderUpdated(val done: Boolean): NoteEvents()
    object StartBuilder: NoteEvents()
    data class EditRequired(val noteUiModel: NoteUiModel): NoteEvents()
    object BioMetricRequired: NoteEvents()
}
