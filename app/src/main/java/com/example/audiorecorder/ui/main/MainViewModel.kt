package com.example.audiorecorder.ui.main

import androidx.lifecycle.ViewModel
import com.example.audiorecorder.ui.main.domain.IStorageDao
import com.example.audiorecorder.ui.main.domain.StorageDao

class MainViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    // Get the default bucket from a custom FirebaseApp
    private val storageReference: IStorageDao by lazy { StorageDao() }

    fun uploadVoice() {
        storageReference.uploadVoice()
    }
}