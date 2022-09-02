package com.example.audiorecorder.utils

import java.text.SimpleDateFormat
import java.util.*

object TimerDateFormat {
    val sdf = SimpleDateFormat("mm:ss", Locale.KOREA)
    fun makeLongToTimerFormat(longTime: Long): String = sdf.format(longTime)
}
