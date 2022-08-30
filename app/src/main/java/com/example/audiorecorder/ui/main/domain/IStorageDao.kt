package com.example.audiorecorder.ui.main.domain

interface IStorageDao {
    suspend fun uploadVoice()
}