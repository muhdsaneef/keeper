package com.saneef.keeper.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saneef.keeper.model.NoteUiModel
import com.saneef.keeper.ui.theme.Background
import com.saneef.keeper.ui.theme.Shapes

@Composable
fun NotesBuilderHome(viewModel: NotesViewModel, noteUiModel: NoteUiModel? = null) {
    val titleMutableState = remember { mutableStateOf(noteUiModel?.title ?: "") }
    val descriptionMutableState = remember { mutableStateOf(noteUiModel?.description ?: "") }
    val isEditMode = remember {
        mutableStateOf(noteUiModel != null)
    }

    Scaffold(
        content = {
            NotesBuilderContent(titleMutableState, descriptionMutableState)
        },
        bottomBar = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)) {
                if (isEditMode.value) {
                    Button(
                        modifier = Modifier.weight(1f),
                        shape = Shapes.large,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                        onClick = { viewModel.deleteNote(noteUiModel?.id) }
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .height(48.dp)
                                .align(Alignment.CenterVertically),
                            text = "Delete",
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.h6
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .padding(top = 6.dp, bottom = 6.dp),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                Button(
                    modifier = Modifier
                        .weight(1f),
                    shape = Shapes.large,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                    onClick = { viewModel.addNote(titleMutableState.value, descriptionMutableState.value) }
                ) {
                    Text(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .height(48.dp)
                            .align(Alignment.CenterVertically),
                        text = "Save",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.h6
                    )
                }
            }
        }
    )
}

@Composable
fun NotesBuilderContent(title: MutableState<String>, description: MutableState<String>) {
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            value = title.value,
            onValueChange = { title.value = it },
            label = { Text("Title", fontFamily = FontFamily.Monospace) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            textStyle = TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 20.sp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Background,
                focusedBorderColor = Background,
                focusedLabelColor = Color.White,
                cursorColor = Color.White,
                unfocusedLabelColor = Background
            ),
        )

        val localFocusManager = LocalFocusManager.current

        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.7f),
            value = description.value,
            onValueChange = { description.value = it },
            label = { Text(text = "Description", fontFamily = FontFamily.Monospace) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    localFocusManager.clearFocus(force = true)
                }
            ),
            textStyle = TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 16.sp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Background,
                focusedBorderColor = Background,
                focusedLabelColor = Color.White,
                cursorColor = Color.White,
                unfocusedLabelColor = Background
            ),
        )
    }
}
