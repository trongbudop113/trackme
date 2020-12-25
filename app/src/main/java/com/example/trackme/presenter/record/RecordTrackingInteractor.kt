package com.example.trackme.presenter.record

interface RecordTrackingInteractor {
    interface View{
        fun showProgressRecord()
        fun hideProgressRecord()
        fun changeStatusPauseToStop()
        fun changeStatusReFreshToPause()
        fun onRecordFailure(message: String)
        fun onRecordSuccess()
        fun onProgressRecord()
    }

    interface Presenter {
        fun onClickStop()
        fun onClickRefresh()
        fun onClickPause()
        fun onStartRecord()
    }

    interface Interactor {
        fun onClickStop()
        fun onClickRefresh()
        fun onClickPause()
        fun onStartRecord()
        fun onStopRecord()
    }

    interface onGetDataListener {
        fun onRecordFailure(message: String?)
        fun onRecordSuccess()
        fun changeStatusPauseToStop()
        fun changeStatusReFreshToPause()
        fun onProgressRecord()
    }
}