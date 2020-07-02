package com.ychong.lan_file_sharing.common.network

import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody

abstract class BaseObserver : Observer<ResponseBody>{
    override fun onComplete() {

    }
    override fun onNext(t: ResponseBody) {
        success(t)
    }

    override fun onError(e: Throwable) {
        error(e)
    }

    override fun onSubscribe(d: Disposable) {

    }

    abstract fun success(t:ResponseBody)
    abstract fun error(e:Throwable)
}