package com.example.audiorecorder.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiorecorder.domain.IStorageDao
import com.example.audiorecorder.domain.StorageDao
import com.example.audiorecorder.dto.Voice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VoiceViewModel : ViewModel() {


    companion object {
        private const val TAG: String = "로그"
    }

    private val _voices = MutableLiveData<List<Voice>>()
    val voices: LiveData<List<Voice>>
        get() = this._voices
    private val storageReference: IStorageDao by lazy { StorageDao() }


    // uri 에 위치한 음성을 업로드함.
    suspend fun uploadVoice(uri: Uri) {
        delay(3000)
        storageReference.uploadVoice(uri)
    }

    // 모든 음성 목록을 가져옴
    fun getAllVoices() {
        Log.d(TAG,"VoiceViewModel - getAllVoices() called")
        viewModelScope.launch(Dispatchers.IO) {
            val listOfVoice = storageReference.getAllVoices()
            Log.d(TAG,"VoiceViewModel - $listOfVoice")
            _voices.postValue(listOfVoice)
        }
    }

    // 음성 라이브 스트리밍
    suspend fun getDownloadLinkOfVoice(uri: Uri): Uri {
        Log.d(TAG,"VoiceViewModel - getDownloadLinkOfVoice() called")
        return storageReference.getDownloadLinkOfVoice(uri)
    }
}