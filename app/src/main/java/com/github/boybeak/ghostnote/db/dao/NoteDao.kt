package com.github.boybeak.ghostnote.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.boybeak.ghostnote.db.entity.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM note WHERE inTrashBin = 0 ORDER BY createAt DESC")
    fun getNotes(): Flow<List<Note>>
    @Insert
    suspend fun createNotes(vararg note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Delete
    suspend fun deleteNotes(vararg notes: Note)

    @Delete
    suspend fun deleteNotes(notes: List<Note>)
}