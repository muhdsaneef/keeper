package com.saneef.keeper.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.saneef.keeper.model.NoteUiModel
import com.saneef.keeper.ui.theme.Background
import com.saneef.keeper.ui.theme.Description
import com.saneef.keeper.ui.theme.KeeperTheme
import com.saneef.keeper.ui.theme.Title
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.concurrent.Executor

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val viewModel: NotesViewModel by viewModels()

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private val promptInfo: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric for notes reveal")
        .setSubtitle("Please use your biometric to reveal the notes.")
        .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KeeperTheme {
                Surface(color = Color.Black) {
                    NotesHome(viewModel)
                }
            }
        }
        initBiometricAuthenticator()
        observeViewModelChanges()
    }

    private fun initBiometricAuthenticator() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    viewModel.onBiometricAuthenticationSucceeded()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
    }

    private fun observeViewModelChanges() {
        lifecycleScope.launchWhenStarted {
            viewModel.noteEventsViewState.collect { event ->
                when (event) {
                    NoteEvents.NoSearchResults -> showSearchResultsNotFoundToast()
                    NoteEvents.BioMetricRequired -> showBiometricDialog()
                    is NoteEvents.EditRequired -> openNotesBuilder(event.noteUiModel)
                    NoteEvents.StartBuilder -> openNotesBuilder()
                    else -> { /* Ignore */ }
                }
            }
        }
    }

    private fun showSearchResultsNotFoundToast() {
        Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show()
    }

    private fun showBiometricDialog() {
        biometricPrompt.authenticate(promptInfo)
    }

    private fun openNotesBuilder(noteUiModel: NoteUiModel? = null) {
        val intent = Intent(this, NotesActivity::class.java).apply {
            if (noteUiModel != null) {
                putExtra(EXTRA_NOTE, noteUiModel)
            }
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_NOTE = "EXTRA_NOTE"
    }
}

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
                        description = if (notesVisibility) note.description else "****",
                        onClicked = { viewModel.onEditClicked(note) },
                        onLongPressed = { viewModel.deleteNote(note.id) }
                    )
                }
        }
    }
}

@Composable
fun Note(title: String, description: String, onClicked: () -> Unit, onLongPressed: () -> Unit) {

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = Background,
        elevation = 2.dp,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = { onClicked() },
                onLongPress = { onLongPressed() }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                color = Title,
                style = MaterialTheme.typography.h6,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = description,
                color = Description,
                style = MaterialTheme.typography.body1,
                fontFamily = FontFamily.Monospace
            )
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
                onValueChange = { query = it },
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
