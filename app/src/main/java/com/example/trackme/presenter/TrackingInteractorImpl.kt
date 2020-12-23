package com.example.trackme.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.example.trackme.database.ProjectDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TrackingInteractorImpl(mOnGetDataListener: TrackingInteractor.onGetDataListener) : TrackingInteractor.Interactor {

    private var mOnGetDataListener : TrackingInteractor.onGetDataListener? = null
    private var database : ProjectDatabase? = null

    init {
        this.mOnGetDataListener = mOnGetDataListener
    }

    @SuppressLint("CheckResult")
    override fun initDataTrackingCall(context: Context?) {
        database = ProjectDatabase.invoke(context!!)
        database?.allTrackDao()?.getAllTracks()
            ?.observeOn(Schedulers.io())
            ?.subscribeOn(AndroidSchedulers.mainThread())
            ?.subscribe(
                { it -> mOnGetDataListener?.onTrackingSuccess(it)},
                { t -> mOnGetDataListener?.onAddTrackingFailure(t.message) }
            )
    }

}