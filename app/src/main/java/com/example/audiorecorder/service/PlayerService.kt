package com.example.audiorecorder.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.audiorecorder.R

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
        val uriOfVoice = intent?.getStringExtra("uri")
        val title = intent?.getStringExtra("title")
        val notification = createNotification(title.toString())
        uriOfVoice?.let { setMediaPlayer(uriOfVoice) }
        startForeground(2, notification)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification(title: String) = NotificationCompat
        .Builder(this.baseContext, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle(title)
        .build()

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