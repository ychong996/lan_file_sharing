package com.ychong.lan_file_sharing.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import android.widget.Toast
import com.ychong.lan_file_sharing.common.BaseConstant
import com.ychong.lan_file_sharing.utils.FtpUtils
import com.ychong.lan_file_sharing.utils.SPUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.functions.Function

class DownloadService:Service() {
    val handler = @SuppressLint("HandlerLeak")
    object : Handler(){
        override fun handleMessage(msg: Message) {
            when(msg.what){
                -1 ->{
                    val errorMsg = msg.obj as String?
                   Log.e("DownloadService","$errorMsg")
                }
                -2 ->{
                    val errorMsg = msg.obj as String?
                    Log.e("DownloadService","$errorMsg")
                }
            }
        }
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("CheckResult")
    override fun onCreate() {
        super.onCreate()

        val ftpAccount = SPUtils.getInstance(this).getString(BaseConstant.SP_FTP_ACCOUNT)
        val ftpPassword = SPUtils.getInstance(this).getString(BaseConstant.SP_FTP_PASSWORD)
        val ftpIP = SPUtils.getInstance(this).getString(BaseConstant.SP_FTP_IP)
        val ftpPort = SPUtils.getInstance(this).getInt(BaseConstant.SP_FTP_PORT)

        Thread(Runnable {
            if (FtpUtils.instance.connect(ftpIP!!,ftpPort)){
                if (FtpUtils.instance.login(ftpAccount!!,ftpPassword!!)){
                    //开始下载FTP文件
                    FtpUtils.instance.download("")
                }else{
                val msg = Message.obtain()
                    msg.what = -1
                    msg.obj = "FTP登陆失败"
                    handler.sendMessage(msg)
                }
            }else{
                val msg = Message.obtain()
                msg.what = -2
                msg.obj = "FTP连接失败"
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun onDestroy() {
        super.onDestroy()
        FtpUtils.instance.destroy()
    }

    public class Binder : android.os.Binder(){
        public fun getService(): Binder {
            return this
        }
    }
}