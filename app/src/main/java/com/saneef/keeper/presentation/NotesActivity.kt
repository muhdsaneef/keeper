package com.saneef.keeper.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.saneef.keeper.model.NoteUiModel
import com.saneef.keeper.presentation.MainActivity.Companion.EXTRA_NOTE
import com.saneef.keeper.ui.theme.KeeperTheme
import com.saneef.keeper.ui.theme.Shapes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NotesActivity : ComponentActivity() {

    private val viewModel: NotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val noteUiModel = intent.extras?.getParcelable<NoteUiModel>(EXTRA_NOTE)

        viewModel.onNoteBuilderCreated(noteUiModel)
        observeViewModelChanges()

        setContent {
            KeeperTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = Color.Black) {
                    NotesBuilderHome(viewModel, noteUiModel)
                }
            }
        }
    }

    private fun observeViewModelChanges() {
        lifecycleScope.launchWhenStarted {
            viewModel.noteAddedSignalViewState.collect { done ->
                if (done) {
                    finish()
                } else {
                    Log.d(NotesActivity::class.java.simpleName, "Error while adding note")
                }
            }
        }
    }
}

@Composable
fun NotesBuilderHome(viewModel: NotesViewModel, noteUiModel:NoteUiModel? = null) {
    val titleMutableState = remember { mutableStateOf(noteUiModel?.title ?: "") }
    val descriptionMutableState = remember { mutableStateOf(noteUiModel?.description ?: "") }

    Scaffold(
        content = {
            NotesBuilderContent(titleMutableState, descriptionMutableState)
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = Shapes.large,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                onClick = { viewModel.addNote(titleMutableState.value, descriptionMutableState.value) }
            ) {
                Text(modifier = Modifier.padding(vertical = 8.dp), text = "Save", color = Color.White)
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
            label = { Text("Title") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        val localFocusManager = LocalFocusManager.current

        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.7f),
            value = description.value,
            onValueChange = { description.value = it },
            label = { Text(text = "Description") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    localFocusManager.clearFocus(force = true)
                }
            )
        )
    }
}
