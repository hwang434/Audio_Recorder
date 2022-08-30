package com.example.audiorecorder.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiorecorder.ui.main.domain.IStorageDao
import com.example.audiorecorder.ui.main.domain.StorageDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    // Get the default bucket from a custom FirebaseApp
    private val storageReference: IStorageDao by lazy { StorageDao() }

    fun uploadVoice() {
        viewModelScope.launch(Dispatchers.IO) {
            storageReference.uploadVoice()
        }
    }
}