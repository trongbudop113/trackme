package com.example.trackme.common

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import com.example.trackme.remote.IGeoCoordinates
import com.example.trackme.remote.RetrofitClient
import java.util.concurrent.TimeUnit


object Common {

    const val REQUEST_CODE = 9999
    const val BASE_URL = "https://maps.googleapis.com/"
    const val KEY_MAP = "AIzaSyDJzuqDUiNT4V7T2eyin6eCrbXmiVOEfIQ"


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

    fun getGeoCodeService(): IGeoCoordinates? {
        return RetrofitClient.getClient(BASE_URL)?.create(IGeoCoordinates::class.java)
    }

    fun scaleBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val scaleBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        val scaleX = newWidth / bitmap.width.toFloat()
        val scaleY = newHeight / bitmap.height.toFloat()
        val pivotX = 0f
        val pivotY = 0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY)
        val canvas = Canvas(scaleBitmap)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bitmap,
            0f,
            0f,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )
        return scaleBitmap
    }
}