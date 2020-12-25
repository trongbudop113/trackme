package com.example.trackme.presenter.tracking

import android.content.Context
import com.example.trackme.model.Tracks

interface TrackingInteractor {
    interface View{
        fun showProgressTracking()
        fun hideProgressTracking()
        fun onTrackingFailure(message: String)
        fun onTrackingSuccess(listTracks : List<Tracks>)
    }

    interface Presenter {
        fun addDataListTracking(context: Context?)
    }

    interface Interactor {
        fun initDataTrackingCall(context: Context?)
    }

    interface onGetDataListener {
        fun onAddTrackingFailure(message: String?)
        fun onTrackingSuccess(listTracks : List<Tracks>)
    }
}