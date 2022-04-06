package com.saneef.keeper.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Surface
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.saneef.keeper.model.NoteUiModel
import com.saneef.keeper.presentation.MainActivity.Companion.EXTRA_NOTE
import com.saneef.keeper.ui.theme.KeeperTheme
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
            viewModel.noteEventsViewState.collect { event ->
                if (event is NoteEvents.BuilderUpdated) {
                    if (event.done) {
                        finish()
                    } else {
                        Log.d(NotesActivity::class.java.simpleName, "Error while adding note")
                    }
                }
            }
        }
    }
}
