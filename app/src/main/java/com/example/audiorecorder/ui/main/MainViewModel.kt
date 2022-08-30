package com.example.audiorecorder.ui.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.audiorecorder.ui.main.domain.IStorageDao
import com.example.audiorecorder.ui.main.domain.StorageDao
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