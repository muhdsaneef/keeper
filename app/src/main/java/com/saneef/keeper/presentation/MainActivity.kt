package com.saneef.keeper.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.material.Surface
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.saneef.keeper.model.NoteUiModel
import com.saneef.keeper.ui.theme.KeeperTheme
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
