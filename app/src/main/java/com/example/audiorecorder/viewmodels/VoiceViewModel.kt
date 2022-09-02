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
import com.example.audiorecorder.utils.Resource
import kotlinx.coroutines.*

class VoiceViewModel : ViewModel() {


    companion object {
        private const val TAG: String = "로그"
    }

    private val _voices = MutableLiveData<List<Voice>>()
    val voices: LiveData<List<Voice>>
        get() = this._voices
    private val storageReference: IStorageDao by lazy { StorageDao() }
    // 타이머 시작 시각
    private val _statTime = MutableLiveData<Resource<Long>>()
    val startTime: LiveData<Resource<Long>>
        get() = _statTime
    // 녹음 중이면 true 아니면 false
    private val _isRecording = MutableLiveData<Boolean>()
    val isRecording: LiveData<Boolean>
        get() = _isRecording

    init {
        _isRecording.value = false
    }

    // uri 에 위치한 음성을 업로드함.
    suspend fun uploadVoice(uri: Uri) {
        Log.d(TAG,"VoiceViewModel - uploadVoice() called")
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

    fun startTimer(time: Long) {
        Log.d(TAG,"VoiceViewModel - startTimer() called")
        changeRecordingStatus()
        updateStartTime(time)
    }

    // 녹음 상태를 반대로 변경함.
    private fun changeRecordingStatus() {
        Log.d(TAG,"VoiceViewModel - changeRecordingStatus() called")
        _isRecording.value = !(_isRecording.value!!)
        Log.d(TAG,"VoiceViewModel - _isRecording.value : ${_isRecording.value}() called")
    }

    // 음성 녹음을 시작한 시각을 계속 post해서 UI를 업데이트함.
    private fun updateStartTime(time: Long) {
        Log.d(TAG,"VoiceViewModel - updateStartTime() called")

        viewModelScope.launch(Dispatchers.IO) {
            // 녹음 중인 동안에만 UI를 업데이트함.
            while (_isRecording.value == true) {
                yield()
                _statTime.postValue(Resource.Success(time))
                delay(1000)
            }
        }
    }

    fun stopTimer() {
        Log.d(TAG,"VoiceViewModel - stopTimer()  isRecoring.value : ${_isRecording.value}called")
        _isRecording.value = false
        _statTime.value = Resource.Success(0)
    }
}