package com.example.audiorecorder.domain

import android.net.Uri

interface IStorageDao {
    suspend fun uploadVoice(uri: Uri): Boolean
}