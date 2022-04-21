package com.saneef.keeper.domain

import com.saneef.keeper.model.DbNote
import com.saneef.keeper.model.NoteUiModel
import com.saneef.keeper.model.TodoItemUiModel

interface NotesDbMapper {

    fun map(noteUiModel: NoteUiModel): DbNote

    fun mapTodoList(todoList: List<TodoItemUiModel>): String
}
