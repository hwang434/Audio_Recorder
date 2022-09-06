package com.example.audiorecorder.domain

import android.net.Uri
import com.example.audiorecorder.dto.Voice

interface IStorageDao {
    suspend fun uploadVoice(uri: Uri): Boolean
    suspend fun getAllVoices(): MutableList<Voice>
    suspend fun getDownloadLinkOfVoice(uri: Uri): Uri
}