package com.github.boybeak.ghostnote.ext

import android.content.Context
import com.github.boybeak.ghostnote.App
import com.github.boybeak.ghostnote.db.AppDatabase

val Context.database: AppDatabase get() {
    return (this.applicationContext as App).database
}