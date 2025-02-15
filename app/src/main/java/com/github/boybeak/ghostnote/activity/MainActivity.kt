package com.github.boybeak.ghostnote.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.boybeak.ghostnote.compose.MainView
import com.github.boybeak.ghostnote.ext.database
import com.github.boybeak.ghostnote.ui.theme.GhostNoteTheme
import com.github.boybeak.ghostnote.vm.MainVM

class MainActivity : ComponentActivity() {

    private val mainVM: MainVM by viewModels<MainVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GhostNoteTheme {
                MainView()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GhostNoteTheme {
        Greeting("Android")
    }
}