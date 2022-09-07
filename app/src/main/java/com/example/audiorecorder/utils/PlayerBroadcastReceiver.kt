package com.example.audiorecorder.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.audiorecorder.service.PlayerService

class PlayerBroadcastReceiver: BroadcastReceiver() {
    companion object {
        private const val TAG: String = "로그"
    }

    override fun onReceive(context: Context?, p1: Intent?) {
        Log.d(TAG,"PlayerBroadcastReceiver - onReceive() called")

        context?.let {
            val intent = Intent(it, PlayerService::class.java)
            intent.putExtra("status", "stop")
            it.startService(intent)
        }
    }
}