package com.example.trackme.presenter

import android.content.Context

interface TrackingInteractor {
    interface View{
        fun showProgressTracking()
        fun hideProgressTracking()
        fun onTrackingFailure(message: String)
        fun onTrackingSuccess()
    }

    interface Presenter {
        fun addDataListTracking(context: Context?)
    }

    interface Interactor {
        fun initDataTrackingCall(context: Context?)
    }

    interface onGetDataListener {
        fun onAddTrackingFailure(message: String?)
        fun onTrackingSuccess()
    }
}