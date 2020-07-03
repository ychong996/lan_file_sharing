package com.ychong.lan_file_sharing.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.ychong.lan_file_sharing.R
import com.ychong.lan_file_sharing.base.BaseActivity
import com.ychong.lan_file_sharing.common.BaseConstant
import com.ychong.lan_file_sharing.common.network.ApiService
import com.ychong.lan_file_sharing.common.network.RetrofitFactory
import com.ychong.lan_file_sharing.data.BaseResp
import com.ychong.lan_file_sharing.data.LoginResp
import com.ychong.lan_file_sharing.databinding.ActivityLoginBinding
import com.ychong.lan_file_sharing.utils.SPUtils
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.view.*
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody

class LoginAct : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding
     override fun initLayout(){
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
     override fun initData(){

    }
     override fun initListener(){
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
            .subscribe(object : Observer<BaseResp<LoginResp>>{
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(baseResp: BaseResp<LoginResp>) {
                    Log.e("登陆返回数据",baseResp.toString())
                    if (baseResp.success){
                        saveData(baseResp.resultBody)
                        startActivity(Intent(this@LoginAct,MainActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this@LoginAct,baseResp.errorMsg,Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(e: Throwable) {
                    Log.e("登陆失败",e.message)
                }

            })
    }
    private fun saveData(loginResp: LoginResp){
        SPUtils.getInstance(this).putString(BaseConstant.SP_FTP_ACCOUNT,loginResp.ftpAccount)
        SPUtils.getInstance(this).putString(BaseConstant.SP_FTP_PASSWORD,loginResp.ftpPassword)
        SPUtils.getInstance(this).putString(BaseConstant.SP_FTP_IP,loginResp.ftpIp)
        SPUtils.getInstance(this).putInt(BaseConstant.SP_FTP_PORT,loginResp.ftpPort)

    }


}