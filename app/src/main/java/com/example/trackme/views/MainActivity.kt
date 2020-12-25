package com.example.trackme.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trackme.R
import com.example.trackme.adapter.TrackingAdapter
import com.example.trackme.model.Tracks
import com.example.trackme.presenter.tracking.TrackingInteractor
import com.example.trackme.presenter.tracking.TrackingPresenterImpl
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), TrackingInteractor.View, TrackingAdapter.OnItemSelected {

    var trackingPresenterImpl : TrackingPresenterImpl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        trackingPresenterImpl = TrackingPresenterImpl(this)
        trackingPresenterImpl?.addDataListTracking(this)
        setListener()
    }

    @SuppressLint("CheckResult")
    private fun setListener() {
        btnRecord.setOnClickListener {
            startActivity(Intent(this, TrackingActivity::class.java))
        }
    }

    override fun showProgressTracking() {
        progress_main.visibility = View.VISIBLE
    }

    override fun hideProgressTracking() {
        progress_main.visibility = View.INVISIBLE
    }

    override fun onTrackingFailure(message: String) {

    }

    override fun onTrackingSuccess(listTracks: List<Tracks>) {
        recyclerViewTracking.setHasFixedSize(true)
        recyclerViewTracking.layoutManager = LinearLayoutManager(this)
        val trackingAdapter = TrackingAdapter(listTracks, this, this)
        recyclerViewTracking.adapter = trackingAdapter
    }

    override fun onSelectedTracking(item: Tracks) {

    }
}