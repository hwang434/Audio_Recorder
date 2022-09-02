package com.example.audiorecorder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.example.audiorecorder.service.RecordService

class AudioRecordApp: Application() {


    companion object {
        private const val TAG: String = "로그"
    }

    override fun onCreate() {
        Log.d(TAG,"AudioRecordApp - onCreate() called")
        super.onCreate()

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                RecordService.CHANNEL_ID,
                "녹음용 알림",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = "녹음용 채널입니다."

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}