package com.example.trackme.presenter.record

import com.example.trackme.database.ProjectDatabase

class RecordTrackingInteractorImpl(mOnGetDataListener: RecordTrackingInteractor.onGetDataListener) : RecordTrackingInteractor.Interactor {

    private var mOnGetDataListener : RecordTrackingInteractor.onGetDataListener? = null
    private var database : ProjectDatabase? = null

    init {
        this.mOnGetDataListener = mOnGetDataListener
    }

    override fun onClickStop() {
        mOnGetDataListener?.onRecordSuccess()
    }

    override fun onClickRefresh() {
        mOnGetDataListener?.changeStatusReFreshToPause()
    }

    override fun onClickPause() {
        mOnGetDataListener?.changeStatusPauseToStop()
    }

    override fun onStartRecord() {

    }

    override fun onStopRecord() {

    }


}