package com.example.audiorecorder.utils

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    // 작업에 성공했을 때
    class Success<T>(data: T) : Resource<T>(data)
    // 작업에 실패했을 때
    class Error<T>(data: T? = null, message: String): Resource<T>(data, message)
    // 로딩 중일 때.
    // 작업이 시작 됐을 때 라이브 데이터에 보내서 로딩 화면을 띄운다.
    class Loading<T> : Resource<T>()
}