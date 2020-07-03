package com.ychong.lan_file_sharing.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import com.ychong.lan_file_sharing.common.BaseConstant
import com.ychong.lan_file_sharing.common.EventMsg
import com.ychong.lan_file_sharing.utils.FileUtils
import com.ychong.lan_file_sharing.utils.FtpUtils
import com.ychong.lan_file_sharing.utils.SPUtils
import org.greenrobot.eventbus.EventBus

class FileService : Service() {
    private val TAG = FileService::class.java.simpleName
    private val localPath =
        Environment.getExternalStorageDirectory().absolutePath + "/lan_file_sharing/"
    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1 -> {
                    val msg = msg.obj as String
                    Log.e(TAG, msg)
                }
                -1 -> {
                    val errorMsg = msg.obj as String?
                    Log.e("DownloadService", "$errorMsg")
                }
                -2 -> {
                    val errorMsg = msg.obj as String?
                    Log.e("DownloadService", "$errorMsg")
                }
                -3 -> {
                    val errorMsg = msg.obj as String?
                    Log.e("DownloadService", "$errorMsg")
                }
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @SuppressLint("CheckResult")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val fileName = intent!!.getStringExtra("FileName")
        ftpDownload(fileName)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun ftpDownload(fileName: String) {
        val ftpAccount = SPUtils.getInstance(this).getString(BaseConstant.SP_FTP_ACCOUNT)
        val ftpPassword = SPUtils.getInstance(this).getString(BaseConstant.SP_FTP_PASSWORD)
        val ftpIP = SPUtils.getInstance(this).getString(BaseConstant.SP_FTP_IP)
        val ftpPort = SPUtils.getInstance(this).getInt(BaseConstant.SP_FTP_PORT)
        Thread(Runnable {
            if (FtpUtils.instance.connect(ftpIP!!, ftpPort)) {
                Log.e(TAG, "FTP连接成功")
                if (FtpUtils.instance.login(ftpAccount!!, ftpPassword!!)) {
                    Log.e(TAG, "FTP登陆成功")
                    //开始下载FTP文件
                    val result = FtpUtils.instance.download(fileName)
                    val msg = Message.obtain()
                    if (result) {
                    EventBus.getDefault().post(EventMsg("refresh","fileList"))
                    } else {
                        FileUtils.instance.deleteFile(localPath,fileName)
                        msg.what = -3
                        msg.obj = "下载失败"
                    }
                    handler.sendMessage(msg)
                } else {
                    val msg = Message.obtain()
                    msg.what = -1
                    msg.obj = "FTP登陆失败"
                    handler.sendMessage(msg)
                }
            } else {
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

    public class Binder : android.os.Binder() {
        public fun getService(): Binder {
            return this
        }
    }
}