package com.ychong.lan_file_sharing

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

abstract class BaseObserver<Any> : Observer<Any> {
    override fun onComplete() {

    }

    override fun onSubscribe(d: Disposable) {

    }

    override fun onNext(t: Any) {
        success(t)
    }

    override fun onError(e: Throwable) {
        error(e)
    }

    abstract fun success(t: Any)
    abstract fun error(e: CustomException)
}