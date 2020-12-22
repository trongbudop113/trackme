package com.example.trackme.presenter

import android.content.Context

class TrackingPresenterImpl(mGetDataView : TrackingInteractor.View) : TrackingInteractor.Presenter, TrackingInteractor.onGetDataListener{

    private var mGetDataView : TrackingInteractor.View?= null
    private var mDataInteractorImpl : TrackingInteractorImpl? = null

    init {
        this.mGetDataView = mGetDataView
        mDataInteractorImpl = TrackingInteractorImpl(this)
    }

    override fun addDataListTracking(context: Context?) {

    }

    override fun onAddTrackingFailure(message: String?) {

    }

    override fun onTrackingSuccess() {

    }

}