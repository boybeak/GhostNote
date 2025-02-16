package com.github.boybeak.ghostnote.service

import android.content.Context
import com.github.boybeak.ghostnote.IGhostService
import com.github.boybeak.ghostnote.ext.database

class Ghost(context: Context) : IGhostService.Stub() {

    private val database = context.database
    private val winManager = context.getSystemService(Context.WINDOW_SERVICE)

    override fun showNote(id: Int) {
    }

    override fun updateNote(id: Int) {
    }

    override fun dismissNote(id: Int) {
    }

}