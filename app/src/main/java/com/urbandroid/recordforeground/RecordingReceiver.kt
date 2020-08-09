package com.urbandroid.recordforeground

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class RecordingReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "Broadcast received")
        context?.apply {
            RecordingService.startService(this)
        }
    }
}