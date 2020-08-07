package com.urbandroid.recordforeground

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit

const val TAG : String = "RecordForeground"

const val NOTIFICATION_CHANNEL_FOREGROUND = "foreground"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        startLater.setOnClickListener {
            val time = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10)
            Log.i(TAG, "Scheduling start ${Date(time)}")
            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setExactAndAllowWhileIdle(AlarmManager.RTC, time, PendingIntent.getForegroundService(this, 0, Intent(this, RecordingService::class.java), PendingIntent.FLAG_UPDATE_CURRENT))

            Toast.makeText(this, "Foreground service will start in a minute", Toast.LENGTH_LONG).show()
            finish()
        }

        startNow.setOnClickListener {
            startForegroundService(Intent(this, RecordingService::class.java))
        }

        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}