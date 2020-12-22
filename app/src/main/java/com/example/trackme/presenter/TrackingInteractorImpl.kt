package com.example.trackme.presenter

import android.content.Context
import com.example.trackme.database.ProjectDatabase

class TrackingInteractorImpl(mOnGetDataListener: TrackingInteractor.onGetDataListener) : TrackingInteractor.Interactor {

    private var mOnGetDataListener : TrackingInteractor.onGetDataListener? = null
    private var database : ProjectDatabase? = null

    init {
        this.mOnGetDataListener = mOnGetDataListener
    }

    override fun initDataTrackingCall(context: Context?) {

    }

}