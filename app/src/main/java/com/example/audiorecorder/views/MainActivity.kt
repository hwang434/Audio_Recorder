package com.example.audiorecorder.views

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.audiorecorder.R
import com.example.audiorecorder.utils.PlayerBroadcastReceiver

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG: String = "로그"
    }

    private lateinit var broadcastManager: LocalBroadcastManager
    private lateinit var broadcastReceiver: PlayerBroadcastReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"MainActivity - onCreate() called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setAndRegisterBroadCastForPlayer()
    }

    private fun setBroadCastManager() {
        Log.d(TAG,"MainActivity - setBroadCastManager() called")
        broadcastManager = LocalBroadcastManager.getInstance(this)
    }

    private fun setBroadCastReceiver() {
        Log.d(TAG,"MainActivity - setBroadCastReceiver() called")
        broadcastReceiver = PlayerBroadcastReceiver()
    }

    private fun registerBroadCastReceiver() {
        Log.d(TAG,"MainActivity - registerBroadCastReceiver() called")
        broadcastManager.registerReceiver(broadcastReceiver, IntentFilter("action1"))
    }

    private fun setAndRegisterBroadCastForPlayer() {
        Log.d(TAG,"MainActivity - setAndRegisterBroadCastForPlayer() called")
        setBroadCastManager()
        setBroadCastReceiver()
        registerBroadCastReceiver()
    }
}