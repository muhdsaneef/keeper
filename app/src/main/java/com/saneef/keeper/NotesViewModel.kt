package com.saneef.keeper

import androidx.lifecycle.ViewModel

class NotesViewModel(
    private val notesUseCase: NotesUseCase,
    private val notesRevealUseCase: NotesRevealUseCase,
) : ViewModel() {

}
