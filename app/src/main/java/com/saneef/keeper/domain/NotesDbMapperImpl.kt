package com.saneef.keeper.domain

import com.saneef.keeper.TimestampHelper
import com.saneef.keeper.model.DbNote
import com.saneef.keeper.model.NoteUiModel

class NotesDbMapperImpl(private val timestampHelper: TimestampHelper): NotesDbMapper {

    override fun map(noteUiModel: NoteUiModel): DbNote {
        return DbNote(
            title = noteUiModel.title,
            timestamp = timestampHelper.currentTimestamp,
            description = noteUiModel.description
        )
    }
}
