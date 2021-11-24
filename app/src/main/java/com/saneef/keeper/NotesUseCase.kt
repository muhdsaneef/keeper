package com.saneef.keeper

interface NotesUseCase {

    fun fetchNotes(keyword: String)
    fun insertNote()
    fun deleteNote()
    fun deleteNotes()
    fun updateNote()
}
