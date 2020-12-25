package com.example.trackme.presenter.record

class RecordTrackingPresenterImpl(mGetDataView : RecordTrackingInteractor.View) : RecordTrackingInteractor.Presenter, RecordTrackingInteractor.onGetDataListener{

    private var mGetDataView : RecordTrackingInteractor.View?= null
    private var mDataInteractorImpl : RecordTrackingInteractorImpl? = null
    override fun onClickStop() {
    }

    override fun onClickRefresh() {
        mDataInteractorImpl?.onClickRefresh()
    }

    override fun onClickPause() {
        mDataInteractorImpl?.onClickPause()
    }

    override fun onStartRecord() {
        mDataInteractorImpl?.onStartRecord()
    }

    override fun onRecordFailure(message: String?) {
        mGetDataView?.onRecordFailure(message!!)
    }

    override fun onRecordSuccess() {
        mGetDataView?.onRecordSuccess()
    }

    override fun changeStatusPauseToStop() {
        mGetDataView?.changeStatusPauseToStop()
    }

    override fun changeStatusReFreshToPause() {
        mGetDataView?.changeStatusReFreshToPause()
    }

    override fun onProgressRecord() {
        mGetDataView?.onProgressRecord()
    }


}