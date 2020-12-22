package com.example.trackme.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trackme.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRecord.setOnClickListener {
          setListener()
        }
    }

    private fun setListener() {
        startActivity(Intent(this, TrackingActivity::class.java))
    }
}