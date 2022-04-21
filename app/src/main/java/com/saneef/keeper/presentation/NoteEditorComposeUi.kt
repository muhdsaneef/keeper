package com.saneef.keeper.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saneef.keeper.model.NoteUiModel
import com.saneef.keeper.model.TodoItemUiModel
import com.saneef.keeper.ui.theme.Background
import com.saneef.keeper.ui.theme.Description
import com.saneef.keeper.ui.theme.Shapes
import kotlin.random.Random

@Composable
fun NotesBuilderHome(viewModel: NotesViewModel, noteUiModel: NoteUiModel? = null) {
    val titleMutableState = remember { mutableStateOf(noteUiModel?.title ?: "") }
    val descriptionMutableState = remember { mutableStateOf(noteUiModel?.description ?: "") }
    val todoItemList = remember { mutableStateListOf<TodoItemUiModel>() }
    noteUiModel?.run { todoItemList.addAll(todoList) }
    val isEditMode = remember {
        mutableStateOf(noteUiModel != null)
    }

    Scaffold(
        content = {
            NotesBuilderContent(titleMutableState, descriptionMutableState, todoItemList)
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(1.dp, shape = MaterialTheme.shapes.medium, color = Background)
            ) {
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
                        color = Background
                    )
                }
                Button(
                    modifier = Modifier
                        .weight(1f),
                    shape = Shapes.large,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                    onClick = {
                        viewModel.addNote(titleMutableState.value, descriptionMutableState.value, todoItemList.toList())
                    }
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
fun NotesBuilderContent(
    title: MutableState<String>,
    description: MutableState<String>,
    todoItemList: SnapshotStateList<TodoItemUiModel>,
) {
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
                unfocusedLabelColor = Description
            ),
        )

        val localFocusManager = LocalFocusManager.current

        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .heightIn(max = 240.dp),
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
                unfocusedLabelColor = Description
            ),
        )

        if (todoItemList.isNotEmpty()) {
            var textCoordinates by remember { mutableStateOf<Rect?>(null) }
            Box(modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxWidth()) {
                Box(modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .drawWithoutRect(textCoordinates)
                    .border(width = 1.dp, color = Background, shape = MaterialTheme.shapes.medium)) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .background(Color.Black)
                            .wrapContentSize()
                            .align(Alignment.Center)
                            .heightIn(min = 0.dp, max = 240.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(items = todoItemList, key = { it.id }) { todo ->
                            TodoItem(
                                todoItemUiModel = todo,
                                onRemoveClicked = {
                                    todoItemList.remove(todo)
                                }
                            )
                        }
                    }
                }
                Text(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 12.dp)
                        .onGloballyPositioned {
                            textCoordinates = it.boundsInParent()
                        }
                        .padding(start = 2.dp, end = 2.dp),
                    text = "Todo list",
                    fontFamily = FontFamily.Monospace,
                    color = Description,
                    fontSize = 12.sp,
                )
            }
        }

        Button(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally)
                .border(width = 1.dp, color = Background, shape = MaterialTheme.shapes.medium),
            shape = Shapes.small,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
            onClick = {
                todoItemList.add(TodoItemUiModel(id = Random.nextLong(), "", false))
            }) {
            Icon(modifier = Modifier.size(16.dp, 16.dp).background(color = Background, shape = MaterialTheme.shapes.small), imageVector = Icons.Outlined.Add, tint = Color.White, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "todo item",
                fontFamily = FontFamily.Monospace,
                color = Color.White,
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}

private fun Modifier.drawWithoutRect(rect: Rect?) =
    drawWithContent {
        if (rect != null) {
            clipRect(
                left = rect.left,
                top = rect.top,
                right = rect.right,
                bottom = rect.bottom,
                clipOp = ClipOp.Difference,
            ) {
                this@drawWithContent.drawContent()
            }
        } else {
            drawContent()
        }
    }

@Composable
fun TodoItem(todoItemUiModel: TodoItemUiModel, onRemoveClicked: () -> Unit) {
    val localFocusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .padding(start = 8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            var text by remember { mutableStateOf(TextFieldValue(todoItemUiModel.text)) }
            val checkedState = remember { mutableStateOf(todoItemUiModel.isCompleted) }
            Checkbox(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 4.dp),
                checked = checkedState.value,
                onCheckedChange = {
                    checkedState.value = it
                    todoItemUiModel.isCompleted = it
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Background,
                    checkmarkColor = Color.White,
                    uncheckedColor = Background
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                onValueChange = { newText ->
                    text = newText
                    todoItemUiModel.text = newText.text
                },
                maxLines = 2,
                placeholder = {
                    Text(
                        text = "Todo item",
                        fontFamily = FontFamily.Monospace,
                        color = Background,
                        style = MaterialTheme.typography.body2
                    )
                },
                shape = Shapes.medium,
                textStyle = MaterialTheme.typography.body2.copy(color = Color.White, fontFamily = FontFamily.Monospace),
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = Color.White,
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                trailingIcon = {
                    IconButton(
                        modifier = Modifier
                            .size(width = 24.dp, height = 24.dp)
                            .background(color = Background, shape = MaterialTheme.shapes.small)
                            .padding(all = 4.dp),
                        onClick = { onRemoveClicked() }) {
                        Icon(
                            imageVector = Icons.Outlined.Remove,
                            tint = Color.White,
                            contentDescription = null,
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        localFocusManager.clearFocus(force = true)
                    }
                )
            )
        }
    }
}
