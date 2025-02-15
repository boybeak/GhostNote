package com.github.boybeak.ghostnote

import android.app.Application
import androidx.room.Room
import com.github.boybeak.ghostnote.db.AppDatabase

class App : Application() {
    val database by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "ghost-note.db")
            .build()
    }
}