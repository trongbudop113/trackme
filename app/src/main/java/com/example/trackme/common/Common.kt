package com.example.trackme.common

import android.annotation.SuppressLint
import java.util.concurrent.TimeUnit


object Common {
    const val REQUEST_CODE = 9999

    @SuppressLint("DefaultLocale")
    fun convertMillisecondToTime(millisecond : Long) : String{
        val hms = java.lang.String.format(
            "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisecond),
            TimeUnit.MILLISECONDS.toMinutes(millisecond) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(millisecond)),
            TimeUnit.MILLISECONDS.toSeconds(millisecond) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(millisecond))
        )
        return hms
    }
}