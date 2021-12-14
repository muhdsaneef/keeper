package com.saneef.keeper.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoteUiModel(
    val id: Long = 0L,
    val title: String,
    val description: String,
): Parcelable
