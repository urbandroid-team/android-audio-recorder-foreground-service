package com.urbandroid.recordforeground

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class RecordingService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        Log.i(TAG, "Foreground service created")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channelName = "Foreground"
            val importance = NotificationManager.IMPORTANCE_LOW
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_FOREGROUND, channelName, importance)
            notificationChannel.setShowBadge(false)
            notificationManager.createNotificationChannel(notificationChannel);
        }

        val i = Intent(this, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pi = PendingIntent.getActivity(this, 4242, i, PendingIntent.FLAG_UPDATE_CURRENT)

        val context = this

        var intent = Intent()

        if (Build.VERSION.SDK_INT >= 26) {
            intent = Intent(android.provider.Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
            intent.putExtra(android.provider.Settings.EXTRA_CHANNEL_ID, NOTIFICATION_CHANNEL_FOREGROUND)
            intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } else if (Build.VERSION.SDK_INT >= 23) {
            intent.setClassName("com.android.settings", "com.android.settings.Settings\$AppNotificationSettingsActivity")
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)

            if (Build.VERSION.SDK_INT >= 26) {
                intent.putExtra(android.provider.Settings.EXTRA_CHANNEL_ID, NOTIFICATION_CHANNEL_FOREGROUND)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
        } else if (Build.VERSION.SDK_INT > 16) {
            try {
                intent = Intent()
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
            } catch (e: Exception) {
                Log.e(TAG, "Error", e)
            }

        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_FOREGROUND)
            .setChannelId(NOTIFICATION_CHANNEL_FOREGROUND)
            .setColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
            .setContentIntent(pi)
            .setShowWhen(false)
            .setContentText("Running")

        startForeground(2342, notificationBuilder.build())

        Log.i(TAG, "Foreground service started")

        val minBufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minBufferSize)

        if (recorder.state == AudioRecord.STATE_UNINITIALIZED) {
            // this condition is true even this is called from a process with an active foreground service
            Log.i(TAG, "Recorder uninitialized FAILED")
        } else {
            Log.i(TAG, "Recorder initialized SUCCESS")
        }

        recorder.release()
        stopSelf()
    }
}