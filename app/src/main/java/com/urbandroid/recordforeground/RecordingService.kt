package com.urbandroid.recordforeground

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.*
import java.util.concurrent.TimeUnit


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


        Log.i(TAG, "Foreground service created")

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val h = Handler()

        val i = Intent(this, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pi = PendingIntent.getActivity(this, 4242, i, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_FOREGROUND)
            .setChannelId(NOTIFICATION_CHANNEL_FOREGROUND)
            .setColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
            .setContentIntent(pi)
            .setShowWhen(false)
            .setContentText("Running")

        startForeground(2342, notificationBuilder.build())

        val runnable = Runnable {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)
            Thread.currentThread().name = "Recorder:" + javaClass.simpleName

            val minBufferSize = AudioRecord.getMinBufferSize(48000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

            val recorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                48000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize)

            if (recorder.state == AudioRecord.STATE_UNINITIALIZED) {
                // this condition is true even this is called from a process with an active foreground service
                Log.i(TAG, "Recorder uninitialized FAILED")
                showNotification("Recorder uninitialized FAILED")
            } else {
                Log.i(TAG, "Recorder initialized SUCCESS")
                showNotification("Recorder initialized SUCCESS")
            }

            recorder.release()

            stopSelf()
        }

        val t = Thread(runnable);
        t.start()

        return START_NOT_STICKY
    }

    private fun showNotification(text : String) {
        var builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_FOREGROUND)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Service onDestroy()")
    }

    companion object {
        fun startService(context : Context) {
            context.startForegroundService(Intent(context, RecordingService::class.java))
        }

        fun scheduleStart(context : Context, duration : Long) {
            val time = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(duration)
            Log.i(TAG, "Scheduling start ${Date(time)}")
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setExactAndAllowWhileIdle(AlarmManager.RTC, time, PendingIntent.getForegroundService(context, 0, Intent(context, RecordingService::class.java), PendingIntent.FLAG_UPDATE_CURRENT))
        }
    }

}