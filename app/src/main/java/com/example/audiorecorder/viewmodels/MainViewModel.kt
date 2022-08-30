package com.example.audiorecorder.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.audiorecorder.domain.IStorageDao
import com.example.audiorecorder.domain.StorageDao
import kotlinx.coroutines.delay

class MainViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    // Get the default bucket from a custom FirebaseApp
    private val storageReference: IStorageDao by lazy { StorageDao() }

    suspend fun uploadVoice(uri: Uri) {
        delay(3000)
        storageReference.uploadVoice(uri)
    }
}