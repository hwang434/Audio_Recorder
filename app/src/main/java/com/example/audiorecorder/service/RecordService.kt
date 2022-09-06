package com.example.audiorecorder.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.audiorecorder.R
import com.example.audiorecorder.views.MainActivity

class RecordService : Service() {

    companion object {
        private const val TAG: String = "로그"
        const val CHANNEL_ID = "com.example.audiorecorder.record"
    }
    private lateinit var recorder: MediaRecorder
    private lateinit var originalName: String
    private lateinit var directoryOfVoice: String
    private lateinit var notification: Notification

    override fun onCreate() {
        Log.d(TAG,"RecordService - onCreate() called")
        super.onCreate()
        setDirectoryOfVoice()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"RecordService - onStartCommand() called")
        intent?.getStringExtra("originalName")?.let { name ->
            originalName = name
        }

        setNotification()
        setRecorder(originalName)
        startForeground(1, notification)
        startRecorder()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d(TAG,"RecordService - onBind() called")
        return null
    }

    override fun onDestroy() {
        Log.d(TAG,"RecordService - onDestroy() called")
        super.onDestroy()

        try {
            recorder.reset()
            recorder.release()
        } catch (e: Exception) {
            Log.w(TAG, "onDestroy: ", e)
        }
    }

    private fun setDirectoryOfVoice() {
        directoryOfVoice = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
    }

    private fun setRecorder(fileName: String) {
        // if : 권한 부여 안했으면 -> 녹음 안함.
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(this.baseContext)
        } else {
            MediaRecorder()
        }

        // 저장 될 파일 명
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFile("$directoryOfVoice/$fileName")
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

        try {
            recorder.prepare()
        } catch (e: Exception) {
            Log.w(TAG, "setRecorder: ", e)
            Toast.makeText(baseContext, "레코더 준비 실패", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startRecorder() {
        recorder.start()
    }

    private fun setNotification() {
        val intent = Intent(baseContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            baseContext,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        notification = NotificationCompat.Builder(this.baseContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("녹음중입니다.")
            .setContentIntent(pendingIntent)
            .build()
    }
}