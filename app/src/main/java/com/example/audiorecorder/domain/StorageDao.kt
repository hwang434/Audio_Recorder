package com.example.audiorecorder.domain

import android.net.Uri
import android.util.Log
import com.example.audiorecorder.dto.Voice
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StorageDao: IStorageDao {
    companion object {
        private const val TAG: String = "로그"
    }
    private val voiceDirectory = "voices/"
    
    private val storageReference by lazy { FirebaseStorage.getInstance().reference }
    override suspend fun uploadVoice(uri: Uri, fileName: String): Boolean {
        Log.d(TAG,"StorageDao - uploadVoice() called")
        // 저장될 디렉터리 명
        val directory = StringBuilder(voiceDirectory)

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

    override suspend fun getAllVoices(): MutableList<Voice> {
        Log.d(TAG,"StorageDao - getAllVoices() called")
        val listOfResult = storageReference.child(voiceDirectory).listAll().await()
        val listOfVoice = mutableListOf<Voice>()

        listOfResult.items.forEach { item ->
            Log.d(TAG,"StorageDao - item.name ${item.name}\nitem.storage : ${item.storage}\nitem.toString : $item")
            listOfVoice.add(Voice(fileName = item.name, author = item.parent?.name.toString(), uri = item.toString()))
        }

        return listOfVoice
    }

    override suspend fun getDownloadLinkOfVoice(uri: Uri): Uri {
        Log.d(TAG,"StorageDao - getDownloadLinkOfVoice(uri = $uri) called")
        val gsReference = FirebaseStorage.getInstance().getReferenceFromUrl(Uri.decode(uri.toString()))
        val task = gsReference.downloadUrl
        task.await()

        return task.result
    }

    override suspend fun deleteVoice(fileName: String): Boolean {
        Log.d(TAG,"StorageDao - deleteVoice() called")
        val task = storageReference.child(voiceDirectory).child(fileName).delete()
        task.await()

        return task.isSuccessful
    }
}
