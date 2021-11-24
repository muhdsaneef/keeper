package com.saneef.keeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.saneef.keeper.model.NoteUiModel
import com.saneef.keeper.ui.theme.Background
import com.saneef.keeper.ui.theme.Description
import com.saneef.keeper.ui.theme.KeeperTheme
import com.saneef.keeper.ui.theme.Title

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KeeperTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Notes(listOf(NoteUiModel("title", "descriptiodfsdhfjkshdfjhskdjfhskjdhfksjdhfkjshdjkfhsjdkfhskdjfhksjdhfjksdhfjshdkjfhsjfd")))
                }
            }
        }
    }
}

@Composable
fun Notes(notes: List<NoteUiModel>) {
    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(300) { note ->
            Note(title = notes.first().title, description = notes.first().description)
        }
    }
}

@Composable
fun Note(title: String, description: String) {
    Surface(shape = MaterialTheme.shapes.medium, color = Background, elevation = 2.dp) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)) {
            Text(
                text = title,
                color = Title,
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.padding(horizontal = 16.dp))
            Text(
                text = description,
                color = Description,
                style = MaterialTheme.typography.body1
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KeeperTheme {
        Notes(listOf(NoteUiModel("titlshdfdfgfge", "descriptiodfsdhfjkshdfjhskdjfhskjdhfksjdhfkjshdjkfhsjdkfhskdjfhksjdhfjksdhfjshdkjfhsjfd")))
    }
}
