package com.ychong.lan_file_sharing.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ychong.lan_file_sharing.common.CustomException
import com.ychong.lan_file_sharing.R
import com.ychong.lan_file_sharing.databinding.ActivityMainBinding
import com.ychong.lan_file_sharing.utils.FtpUtils
import com.ychong.lan_file_sharing.utils.SPUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var password: String
    private lateinit var account: String
    private var port: Int = -1
    private var host: String? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        initData()
        initListener()
    }
    private fun initLayout(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    private fun initData(){
        binding.headInclude.titleTv.text = "登陆"
        binding.headInclude.rightTv.visibility = View.VISIBLE
        binding.headInclude.rightTv.setOnClickListener(this)

    }
    private fun initListener() {
        binding.loginBtn.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.rightTv ->{
                startActivity(Intent(this,
                    SettingActivity::class.java))
            }
            R.id.loginBtn ->{
                login()
            }
        }
    }
    @SuppressLint("CheckResult")
    private fun login(){
       if (!checkData())return
        Observable.create(ObservableOnSubscribe<Boolean> { emitter ->
            val isConnect = FtpUtils.instance.connect(host!!, port)
            if (isConnect){
                emitter.onNext(isConnect)
            }else{
                emitter.onError(
                    CustomException(
                        "FTP连接失败"
                    )
                )
            }
        }).flatMap {
            if (FtpUtils.instance.login(account,password)){
                Observable.just(true)
            }else{
                Observable.error(
                    CustomException(
                        "FTP登陆失败"
                    )
                )
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                startActivity(Intent(this,
                    FileManagerActivity::class.java))
                finish()
            }
    }
    private fun checkData():Boolean{
         host = SPUtils.getInstance(this).getString("FTP_IP")
         port = SPUtils.getInstance(this).getInt("FTP_PORT")
         account = binding.accountEt.text.toString()
         password = binding.passwordEt.text.toString()
        if (host.isNullOrEmpty()){
            Toast.makeText(this,"FTP地址有误，请检查",Toast.LENGTH_SHORT).show()
            return false
        }
        if (port==-1){
            Toast.makeText(this,"FTP端口有误，请检查",Toast.LENGTH_SHORT).show()
            return false
        }
        if (account.isEmpty()){
            Toast.makeText(this,"账号有误，请检查",Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()){
            Toast.makeText(this,"密码有误，请检查",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}