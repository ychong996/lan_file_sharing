package com.ychong.lan_file_sharing.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.ychong.lan_file_sharing.R
import com.ychong.lan_file_sharing.common.network.ApiService
import com.ychong.lan_file_sharing.common.network.RetrofitFactory
import com.ychong.lan_file_sharing.databinding.ActivityLoginBinding
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.view.*
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody

class LoginAct : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        initData()
        initListener()
    }
    private fun initLayout(){
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    private fun initData(){

    }
    private fun initListener(){
        binding.loginBtn.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.loginBtn ->{
                login()
            }
            else ->{

            }
        }
    }
    private fun login(){
        val account = binding.accountEt.text.toString()
        val password = binding.passwordEt.text.toString()
        val map = HashMap<String,String>()
        map["account"] = account
        map["password"] = password

        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Gson().toJson(map))
        RetrofitFactory.instance.create(ApiService::class.java).login(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ResponseBody>{
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: ResponseBody) {
                    Log.e("登陆成功",t.string())
                }

                override fun onError(e: Throwable) {
                    Log.e("登陆失败",e.message)
                }

            })


    }


}