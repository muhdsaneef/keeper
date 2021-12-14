package com.saneef.keeper.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saneef.keeper.domain.NotesUseCase
import com.saneef.keeper.model.NoteUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesUseCase: NotesUseCase,
) : ViewModel() {

    private val notesState: MutableStateFlow<List<NoteUiModel>> = MutableStateFlow(emptyList())
    val notes: StateFlow<List<NoteUiModel>>
        get() = notesState.asStateFlow()

    private val buildSignalChannel: Channel<Unit> = Channel(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val buildSignalViewState: Flow<Unit>
        get() = buildSignalChannel.receiveAsFlow()

    private val noteAddedSignalChannel: Channel<Boolean> =
        Channel(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val noteAddedSignalViewState: Flow<Boolean>
        get() = noteAddedSignalChannel.receiveAsFlow()

    private val noteEditRequiredSignalChannel: Channel<NoteUiModel> =
        Channel(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val noteEditRequiredSignalViewState: Flow<NoteUiModel>
        get() = noteEditRequiredSignalChannel.receiveAsFlow()

    private val biometricRequiredSignalChannel: Channel<Unit> =
        Channel(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val biometricRequiredSignalViewState: Flow<Unit>
        get() = biometricRequiredSignalChannel.receiveAsFlow()

    private val notesVisibilityState = MutableStateFlow(false)
    val notesVisibilityViewState: StateFlow<Boolean>
        get() = notesVisibilityState.asStateFlow()

    private var isBiometricVerified: Boolean = false

    private var isEditMode: Boolean = false
    private var noteId: Long = 0L

    fun onNotesHomeCreated() {
        fetchAllNotes()
    }

    fun addNote(title: String, description: String) {
        if (title.isEmpty() || description.isEmpty()) return

        viewModelScope.launch {
            runCatching {
                if (isEditMode) {
                    notesUseCase.updateNote(
                        NoteUiModel(
                            id = noteId,
                            title = title,
                            description = description
                        )
                    )
                } else {
                    notesUseCase.insertNote(
                        NoteUiModel(
                            title = title,
                            description = description
                        )
                    )
                }
                isEditMode = false
                noteAddedSignalChannel.send(true)
            }.onFailure {
                noteAddedSignalChannel.send(false)
            }
        }
    }

    fun searchNote(query: String) {
        if (query.isEmpty()) {
            fetchAllNotes()
        } else {
            viewModelScope.launch {
                withContext(Dispatchers.Default) {
                    notesUseCase.fetchNotes().collect {
                        val searchResults = it.filter { note ->
                            note.title.contains(query, ignoreCase = true) ||
                                note.description.contains(query, ignoreCase = true)
                        }

                        notesState.value = searchResults
                    }
                }
            }
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
            biometricRequiredSignalChannel.send(Unit)
        }
    }

    fun onCreateNotesClicked() {
        viewModelScope.launch {
            buildSignalChannel.send(Unit)
        }
    }

    fun onBiometricAuthenticationSucceeded() {
        isBiometricVerified = true
        notesVisibilityState.value = true
    }

    private fun fetchAllNotes() {
        viewModelScope.launch {
            notesUseCase.fetchNotes().collect {
                notesState.value = it
            }
        }
    }

    fun onEditClicked(noteUiModel: NoteUiModel) {
        viewModelScope.launch {
            noteEditRequiredSignalChannel.send(noteUiModel)
        }
    }

    fun onNoteBuilderCreated(noteUiModel: NoteUiModel?) {
        if (noteUiModel != null) {
            isEditMode = true
            noteId = noteUiModel.id
        }
    }
}
