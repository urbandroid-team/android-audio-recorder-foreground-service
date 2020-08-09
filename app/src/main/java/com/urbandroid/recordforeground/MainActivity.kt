package com.urbandroid.recordforeground

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

const val TAG : String = "RecordForeground"

const val NOTIFICATION_CHANNEL_FOREGROUND = "foreground"

const val ACTION = "com.urbandroid.recordforeground.ACTION"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        startLater.setOnClickListener {
            var duration = 10L
            try {
                duration = Integer.parseInt(minutesText.text.toString()).toLong()
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
            }

            RecordingService.scheduleStart(this, duration)

            Log.i(TAG , "Foreground service will start at ${Date(System.currentTimeMillis() + duration)}")
            Toast.makeText(this, "Foreground service will start at ${Date(System.currentTimeMillis() + duration)}", Toast.LENGTH_LONG).show()
            finish()
        }

        startNow.setOnClickListener {
            RecordingService.startService(this)
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

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Activity onDestroy()")
    }

}