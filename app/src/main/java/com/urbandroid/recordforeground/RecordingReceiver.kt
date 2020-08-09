package com.urbandroid.recordforeground

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

class RecordingReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "Broadcast received")
        context?.apply {
            Log.i(TAG , "Foreground service will start at ${Date(System.currentTimeMillis() + 60000)}")
            RecordingService.scheduleStart(this, 1)
        }
    }
}