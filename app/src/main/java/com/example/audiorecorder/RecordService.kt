package com.example.audiorecorder

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class RecordService : Service() {
    
    companion object {
        private const val TAG: String = "로그"
    }

    override fun onCreate() {
        Log.d(TAG,"RecordService - onCreate() called")
        super.onCreate()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"RecordService - onStartCommand() called")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d(TAG,"RecordService - onBind() called")
        return null
    }

    override fun onDestroy() {
        Log.d(TAG,"RecordService - onDestroy() called")
        super.onDestroy()
    }
}