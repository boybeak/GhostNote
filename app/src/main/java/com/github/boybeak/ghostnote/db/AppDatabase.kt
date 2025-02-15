package com.github.boybeak.ghostnote.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.boybeak.ghostnote.db.dao.NoteDao
import com.github.boybeak.ghostnote.db.entity.Note

@Database(entities = [Note::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}