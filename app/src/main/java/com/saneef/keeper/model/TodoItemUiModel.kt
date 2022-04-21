package com.saneef.keeper.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class TodoItemUiModel(
    val id: Long,
    var text: String,
    var isCompleted: Boolean
): Parcelable
