package com.saneef.keeper.repository

import android.util.Log
import com.saneef.keeper.TimestampHelper
import com.saneef.keeper.dao.NotesDao
import com.saneef.keeper.database.RemoteFirebaseDatabase
import com.saneef.keeper.domain.NotesDbMapper
import com.saneef.keeper.model.DbNote
import com.saneef.keeper.model.NoteUiModel
import com.saneef.keeper.model.TodoItemUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class NotesRepository @Inject constructor(
    private val notesDao: NotesDao,
    private val notesDbMapper: NotesDbMapper,
    private val timestampHelper: TimestampHelper,
    private val firebaseDatabase: RemoteFirebaseDatabase,
    ) {
    val notesFlow: Flow<List<NoteUiModel>>
        get() = notesDao.getAllNotes()
            .map {
                it.map { note ->
                    NoteUiModel(
                        id = note.id,
                        title = note.title,
                        description = note.description,
                        todoList = note.todoList?.let { todoList ->
                            Json.decodeFromString<List<TodoItemUiModel>>(
                                todoList
                            )
                        }.orEmpty()
                    )
                }
            }

    suspend fun uploadCurrentTimestamp() {
        firebaseDatabase.uploadTimeStamp()
    }

    fun insert(note: NoteUiModel) {
        notesDao.insert(notesDbMapper.map(note))
    }

    fun update(note: NoteUiModel) {
        val updatedNote = note.run {
            DbNote(
                id = id,
                title = title,
                timestamp = timestampHelper.currentTimestamp,
                description = description,
                todoList = notesDbMapper.mapTodoList(todoList),
            )
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
