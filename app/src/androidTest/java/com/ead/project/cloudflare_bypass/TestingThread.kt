package com.ead.project.cloudflare_bypass

import android.os.Handler
import android.os.Looper

internal object TestingThread {

    private val handler = Handler(Looper.getMainLooper())

    fun onUi(action: () -> Unit) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            handler.post(action)
        } else {
            action.invoke()
        }
    }
}