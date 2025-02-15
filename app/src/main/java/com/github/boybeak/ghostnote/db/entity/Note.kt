package com.github.boybeak.ghostnote.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) var uid: Long = 0,
    var title: String,
    var text: String,
    var textSize: Float,
    var bgColor: Int,
    var alpha: Float,
    var createAt: Long = System.currentTimeMillis(),
    var modifyAt: Long = createAt,
    var alwaysShow: Boolean,
    var snap: Boolean,
    var inTrashBin: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Note

        return uid == other.uid
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }
}