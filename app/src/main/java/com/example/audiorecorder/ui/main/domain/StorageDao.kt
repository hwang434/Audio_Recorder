package com.example.audiorecorder.ui.main.domain

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage

class StorageDao: IStorageDao {
    companion object {
        private const val TAG: String = "로그"
    }
    private val voiceDirectory = "voices/"
    
    private val storageReference by lazy { FirebaseStorage.getInstance().reference }
    override suspend fun uploadVoice(uri: Uri): Boolean {
        Log.d(TAG,"StorageDao - uploadVoice() called")

        // 저장될 디렉터리 명
        val directory = StringBuilder(voiceDirectory)
        val fullFileName = uri.lastPathSegment?.split("/")
        val fileName = fullFileName?.get(fullFileName.size - 1)

        directory.append(fileName)
        Log.d(TAG,"StorageDao - directory : $directory")
        val ref = storageReference.child("$directory")

        ref.putFile(uri).addOnCompleteListener {
            Log.d(TAG,"StorageDao - isSuccess : ${it.isSuccessful} called")
            if (!it.isSuccessful) {
                throw Exception("오디오 파일 업로드 실패")
            }
        }

        return true
    }
}