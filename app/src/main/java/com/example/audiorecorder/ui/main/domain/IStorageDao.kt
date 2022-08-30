package com.example.audiorecorder.ui.main.domain

import android.net.Uri

interface IStorageDao {
    suspend fun uploadVoice(uri: Uri): Boolean
}