package com.example.audiorecorder.domain

import android.net.Uri
import com.example.audiorecorder.dto.Voice
import com.example.audiorecorder.utils.Resource

interface IStorageDao {
    suspend fun uploadVoice(uri: Uri, fileName: String): Resource<Boolean>
    suspend fun getAllVoices(): MutableList<Voice>
    suspend fun getDownloadLinkOfVoice(uri: Uri): Uri
    suspend fun deleteVoice(fileName: String): Boolean
}