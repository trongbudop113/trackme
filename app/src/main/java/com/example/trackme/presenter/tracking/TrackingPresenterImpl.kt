package com.example.trackme.presenter.tracking

import android.content.Context
import com.example.trackme.model.Tracks
import com.example.trackme.presenter.tracking.TrackingInteractor
import com.example.trackme.presenter.tracking.TrackingInteractorImpl

class TrackingPresenterImpl(mGetDataView : TrackingInteractor.View) : TrackingInteractor.Presenter, TrackingInteractor.onGetDataListener{

    private var mGetDataView : TrackingInteractor.View?= null
    private var mDataInteractorImpl : TrackingInteractorImpl? = null

    init {
        this.mGetDataView = mGetDataView
        mDataInteractorImpl = TrackingInteractorImpl(this)
    }

    override fun addDataListTracking(context: Context?) {
        mGetDataView?.showProgressTracking()
        mDataInteractorImpl?.initDataTrackingCall(context)
    }

    override fun onAddTrackingFailure(message: String?) {
        mGetDataView?.onTrackingFailure(message!!)
        mGetDataView?.hideProgressTracking()
    }

    override fun onTrackingSuccess(listTracks : List<Tracks>) {
        mGetDataView?.onTrackingSuccess(listTracks)
        mGetDataView?.hideProgressTracking()
    }

}