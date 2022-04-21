package com.saneef.keeper.domain

import com.saneef.keeper.TimestampHelper
import com.saneef.keeper.model.DbNote
import com.saneef.keeper.model.NoteUiModel
import com.saneef.keeper.model.TodoItemUiModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NotesDbMapperImpl(private val timestampHelper: TimestampHelper): NotesDbMapper {

    override fun map(noteUiModel: NoteUiModel): DbNote {
        return DbNote(
            title = noteUiModel.title,
            timestamp = timestampHelper.currentTimestamp,
            description = noteUiModel.description,
            todoList = mapTodoList(noteUiModel.todoList),
        )
    }

    override fun mapTodoList(todoList: List<TodoItemUiModel>): String {
        return Json.encodeToString(todoList)
    }
}
