package com.example.audiorecorder.ui.main.domain

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class StorageDao: IStorageDao {
    companion object {
        private val TAG: String = "로그"
    }
    
    private val storageReference by lazy { FirebaseStorage.getInstance().reference }
    override fun uploadVoice() {
        Log.d(TAG,"StorageDao - uploadVoice() called")

        val file = Uri.fromFile(File("/storage/emulated/0/Download/1661764058737.3gp"))
        val ref = storageReference.child("voices/${file.lastPathSegment}")
        ref.putFile(file).addOnCompleteListener { 
            Log.d(TAG,"StorageDao - isSuccess : ${it.isSuccessful} called")
        }
    }
}