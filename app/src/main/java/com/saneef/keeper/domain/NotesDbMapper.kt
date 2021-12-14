package com.saneef.keeper.domain

import com.saneef.keeper.model.DbNote
import com.saneef.keeper.model.NoteUiModel

interface NotesDbMapper {

    fun map(noteUiModel: NoteUiModel): DbNote
}
