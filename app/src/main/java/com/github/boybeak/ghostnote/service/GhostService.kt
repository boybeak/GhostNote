package com.github.boybeak.ghostnote.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.github.boybeak.ghostnote.IGhostService

class GhostService : Service() {

    private val iGhostService: IGhostService.Stub by lazy {
        Ghost(this)
    }

    override fun onBind(intent: Intent): IBinder {
        return iGhostService
    }
}