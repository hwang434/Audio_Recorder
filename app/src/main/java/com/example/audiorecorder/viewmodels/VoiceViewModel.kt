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

    private val _voices = MutableLiveData<MutableList<Voice>>()
    val voices: LiveData<MutableList<Voice>>
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
    // 음악 링크
    private val _linkOfVoice = MutableLiveData<Resource<Voice>>()
    val linkOfVoice: LiveData<Resource<Voice>>
        get() = _linkOfVoice

    init {
        _isRecording.value = false
    }

    // uri 에 위치한 음성을 업로드함.
    suspend fun uploadVoice(uri: Uri, fileName: String) {
        Log.d(TAG,"VoiceViewModel - uploadVoice(uri = $uri) called")
        delay(1000)
        storageReference.uploadVoice(uri, fileName)
    }

    // 모든 음성 목록을 가져옴
    fun getAllVoices() {
        Log.d(TAG,"VoiceViewModel - getAllVoices() called")
        viewModelScope.launch(Dispatchers.IO) {
            val listOfVoice = storageReference.getAllVoices()
            _voices.postValue(listOfVoice)
        }
    }

    // 음성 라이브 스트리밍
    fun refreshLinkOfVoice(title: String, uri: Uri) {
        Log.d(TAG,"VoiceViewModel - refreshLinkOfVoice() called")
        _linkOfVoice.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val downloadLink = storageReference.getDownloadLinkOfVoice(uri)
            withContext(Dispatchers.Main) {
                _linkOfVoice.postValue(Resource.Success(Voice(fileName = title, uri = downloadLink.toString())))
            }
        }
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
    }

    // 음성 녹음을 시작한 시각을 계속 "post"해서 UI를 업데이트함.
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
        Log.d(TAG,"VoiceViewModel - stopTimer() called")
        _isRecording.value = false
        _statTime.value = Resource.Success(0)
    }
}