package com.example.audiorecorder.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.audiorecorder.R
import com.example.audiorecorder.utils.PlayerBroadcastReceiver

class PlayerService : Service() {

    companion object {
        private const val TAG: String = "로그"
        const val CHANNEL_ID = "com.example.audiorecorder.player"
    }
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        Log.d(TAG,"PlayerService - onCreate() called")
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"PlayerService - onStartCommand() called")
        intent?.let { it ->
            // if : status is stop then stop the service.
            if (it.getStringExtra("status") == "stop") {
                stopSelf()
            }

            // else : play the uri voice.
            val uriOfVoice = it.getStringExtra("uri")
            val title = it.getStringExtra("title")
            val notification = createNotification(title.toString())
            uriOfVoice?.let { setMediaPlayer(uriOfVoice) }
            startForeground(2, notification)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification(title: String): Notification {
        Log.d(TAG,"PlayerService - createNotification() called")
        // if click the stop button. then send broadCast to PlayerBroadcastReceiver.
        val stopIntent = Intent(baseContext, PlayerBroadcastReceiver::class.java)
        stopIntent.putExtra("event", "stop")
        val pIntent = PendingIntent.getBroadcast(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat
            .Builder(this.baseContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .addAction(R.drawable.button_stop_record, "stop", pIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0)
            )
            .build()
    }


    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG,"PlayerService - onBind() called")
        return null
    }

    override fun onDestroy() {
        Log.d(TAG,"PlayerService - onDestroy() called")
        super.onDestroy()
        mediaPlayer.reset()
        mediaPlayer.release()
    }

    private fun setMediaPlayer(uriOfVoice: String) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.reset()
            mediaPlayer.release()
        }

        mediaPlayer = MediaPlayer()
        mediaPlayer.apply {
            setDataSource(uriOfVoice)
            setOnPreparedListener { it.start() }
            setOnCompletionListener { stopSelf() }
            prepareAsync()
        }
    }
}