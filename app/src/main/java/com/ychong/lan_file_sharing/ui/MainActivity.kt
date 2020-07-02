package com.ychong.lan_file_sharing.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ychong.lan_file_sharing.R
import com.ychong.lan_file_sharing.common.network.ApiService
import com.ychong.lan_file_sharing.common.network.BaseObserver
import com.ychong.lan_file_sharing.common.network.RetrofitFactory
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        initData()
        initListener()
    }
    private fun initLayout(){
        setContentView(R.layout.activity_main)

    }
    private fun initData(){
        RetrofitFactory.instance.create(ApiService::class.java)
            .getMenuList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver(){
                override fun success(t: ResponseBody) {

                }

                override fun error(e: Throwable) {

                }

            })

    }
    private fun initListener(){

    }
}