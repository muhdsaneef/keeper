package com.saneef.keeper.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.saneef.keeper.ui.theme.Background
import com.saneef.keeper.ui.theme.Description
import com.saneef.keeper.ui.theme.Shapes
import com.saneef.keeper.ui.theme.Title

@Composable
fun NotesHome(viewModel: NotesViewModel) {
    Scaffold(
        topBar = {
            TopBar(viewModel)
        },
        content = {
            Notes(viewModel)
        },
        floatingActionButton = {
            AddNoteFloatingActionButton(viewModel)
        }
    )
}

@Composable
fun AddNoteFloatingActionButton(viewModel: NotesViewModel) {
    FloatingActionButton(
        onClick = { viewModel.onCreateNotesClicked() },
        backgroundColor = Background
    ) {
        Icon(imageVector = Icons.Outlined.Add, contentDescription = null, tint = Color.White)
    }
}

@Composable
fun Notes(viewModel: NotesViewModel) {
    val notes by viewModel.notes.collectAsState(emptyList())
    val notesVisibility by viewModel.notesVisibilityViewState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .padding(all = 16.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (notes.isEmpty()) {
            item {
                EmptyResult()
            }
        } else {
            items(items = notes, key = { it.id }) { note ->
                Note(
                    title = note.title,
                    description = if (notesVisibility) note.description else "Content hidden",
                    onClicked = { viewModel.onEditClicked(note) },
                    onDeleteClicked = { viewModel.deleteNote(note.id) }
                )
            }
        }
    }
}

@Composable
fun Note(title: String, description: String, onClicked: () -> Unit, onDeleteClicked: () -> Unit) {
    var confirmationDialogShown by remember { mutableStateOf(false) }

    if (confirmationDialogShown) {
        ConfirmationDialog(
            onConfirmClicked = { onDeleteClicked() },
            onCancelClicked = { confirmationDialogShown = false },
            onDismissed = { confirmationDialogShown = false }
        )
    }

    Surface(
        color = Color.Transparent,
        elevation = 2.dp,
        modifier = Modifier.border(width = 2.dp, color = Background, shape = MaterialTheme.shapes.medium)) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (noteContent, contentDivider, visibilityIcon) = createRefs()
            Column(
                modifier = Modifier
                    .wrapContentSize(align = Alignment.CenterStart)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .fillMaxWidth()
                    .constrainAs(noteContent) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(contentDivider.start)
                        width = Dimension.fillToConstraints
                    }
                    .clickable { onClicked() }
            ) {
                Text(
                    text = title,
                    color = Title,
                    style = MaterialTheme.typography.h6,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = description,
                    color = Description,
                    style = MaterialTheme.typography.body1,
                    fontFamily = FontFamily.Monospace,
                )
            }
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .padding(vertical = 8.dp)
                    .constrainAs(contentDivider) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(visibilityIcon.start)
                        height = Dimension.fillToConstraints
                    },
                color = Background
            )
            IconButton(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .constrainAs(visibilityIcon) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }, onClick = { confirmationDialogShown = true }) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    tint = Color.White,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
fun TopBar(viewModel: NotesViewModel) {
    TopAppBar(backgroundColor = Background) {
        Box(modifier = Modifier.fillMaxSize()) {

            var query by remember { mutableStateOf("") }

            TextField(
                modifier = Modifier.padding(end = 32.dp),
                value = query,
                onValueChange = {
                    query = it
                    viewModel.searchNote(it)
                },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        modifier = Modifier.clickable {
                            viewModel.searchNote(query = query)
                        },
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = Color.White
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    textColor = Title,
                    cursorColor = Title
                ),
                textStyle = TextStyle(
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                ),
                placeholder = {
                    Text(text = "Search", color = Color.White, fontFamily = FontFamily.Monospace)
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.searchNote(query)
                })
            )
            val notesVisibility by viewModel.notesVisibilityViewState.collectAsState()

            Icon(
                imageVector = if (notesVisibility) {
                    Icons.Outlined.Visibility
                } else {
                    Icons.Outlined.VisibilityOff
                },
                tint = Color.White,
                contentDescription = null,
                modifier = Modifier
                    .padding(all = 16.dp)
                    .align(Alignment.CenterEnd)
                    .clickable {
                        viewModel.toggleNotesVisibility()
                    }
            )
        }
    }
}

@Composable
fun EmptyResult() {
    Text(
        text = "No notes found.",
        color = Color.White,
        modifier = Modifier.fillMaxSize(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.h5
    )
}

@Composable
fun ConfirmationDialog(onConfirmClicked: () -> Unit, onCancelClicked: () -> Unit, onDismissed: () -> Unit) {
    AlertDialog(
        modifier = Modifier.border(width = 2.dp, color = Background, shape = MaterialTheme.shapes.medium),
        onDismissRequest = { onDismissed() },
        buttons = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .border(width = 1.dp, color = Background, shape = MaterialTheme.shapes.medium),
                    shape = Shapes.small,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                    onClick = { onConfirmClicked() }
                ) {
                    Text(text = "Confirm", color = Color.White, fontFamily = FontFamily.Monospace)
                }
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .border(width = 1.dp, color = Background, shape = MaterialTheme.shapes.medium),
                    shape = Shapes.small,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                    onClick = { onCancelClicked() }
                ) {
                    Text(text = "Cancel", color = Color.White, fontFamily = FontFamily.Monospace)
                }
            }
        },
        backgroundColor = Color.Black,
        title = {
            Text(text = "Do you want to delete?", fontFamily = FontFamily.Monospace)
        },
        text = {
            Text(text = "This will permanently remove the note from the database.", fontFamily = FontFamily.Monospace)
        },
        contentColor = Color.White
    )
}
