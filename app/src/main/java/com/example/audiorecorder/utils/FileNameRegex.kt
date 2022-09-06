package com.example.audiorecorder.utils

import android.util.Log
import java.util.regex.Pattern

object FileNameRegex {

    private const val TAG: String = "로그"
    private val PATTERN = Pattern.compile("^[ㄱ-ㅎ가-힣a-zA-Z0-9_]+$")

    fun isValidFileName(fileName: String): Boolean {
        Log.d(TAG,"FileNameRegex - isValidFileName() called")
        val matcher = PATTERN.matcher(fileName)
        return matcher.matches()
    }
}