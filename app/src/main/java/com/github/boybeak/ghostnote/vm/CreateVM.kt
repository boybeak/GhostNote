package com.github.boybeak.ghostnote.vm

import android.content.Context
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.boybeak.ghostnote.db.entity.Note
import com.github.boybeak.ghostnote.ext.database
import kotlinx.coroutines.launch

class CreateVM : ViewModel() {
    val title = mutableStateOf("")
    val text = mutableStateOf("")
    val textSize = mutableFloatStateOf(14F)
    val bgColor = mutableIntStateOf(Color.Transparent.toArgb())
    val alpha = mutableFloatStateOf(1F)
    val alwaysShow = mutableStateOf(false)
    val snap = mutableStateOf(false)

    val showColorPicker = mutableStateOf(false)
    val showTextSizeSlider = mutableStateOf(false)

    val isReady get() = title.value.isNotBlank() || text.value.isNotBlank()

    fun create(context: Context): Note {
        val note = Note(
            title = title.value,
            text = text.value,
            textSize = textSize.floatValue,
            bgColor = bgColor.intValue,
            alpha = alpha.floatValue,
            alwaysShow = alwaysShow.value,
            snap = snap.value,
            inTrashBin = false
        )

        viewModelScope.launch {
            context.database.noteDao().createNotes(note)
        }

        return note
    }
}