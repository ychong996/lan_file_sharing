package com.ychong.lan_file_sharing.common.network

import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody

abstract class BaseObserver<T> : Observer<T>{
    override fun onComplete() {

    }
    override fun onNext(t: T) {
        success(t)
    }

    override fun onError(e: Throwable) {
        error(e)
    }

    override fun onSubscribe(d: Disposable) {

    }

    abstract fun success(t:T)
    abstract fun error(e:Throwable)
}