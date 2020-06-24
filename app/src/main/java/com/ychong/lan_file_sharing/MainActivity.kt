package com.ychong.lan_file_sharing

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ychong.lan_file_sharing.databinding.ActivityMainBinding
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: FileListAdapter
    private var fileList: MutableList<FileBean> = ArrayList<FileBean>()

    private var operationPosition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            Thread(Runnable {
                val host = binding.ftpIpEt.text.toString()
                val port = binding.ftpPortEt.text.toString().toInt()
                val account = binding.accountEt.text.toString()
                val password = binding.passwordEt.text.toString()
                val isConnect = FtpUtils.instance.connect(host, port)
                if (!isConnect){
                    Log.e("FTP ","连接失败")
                    return@Runnable
                }
                Log.e("FTP ", "连接成功，准备登录")
                val isLogin = FtpUtils.instance.login(account, password)
                if (!isLogin){
                    Log.e("FTP ","登陆失败")
                    return@Runnable
                }
                Log.e("FTP ", "登录成功，开始获取数据")
                val files = FtpUtils.instance.getFileList()
                if (!files.isNullOrEmpty()) {
                    fileList.clear()
                    for (item in files){
                        fileList.add(FileBean(item))
                    }
                    val message = Message.obtain()
                    message.what = 0
                    handler.sendMessage(message)

                }
            }).start()
        }
        adapter = FileListAdapter(this,fileList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.recyclerView.adapter = adapter

    }

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {
                    adapter.notifyDataSetChanged()
                }
                1 -> {
                    val fileBean = msg.obj as FileBean
                    if (fileBean.isDownload) {
                        Toast.makeText(this@MainActivity, "下载成功", Toast.LENGTH_SHORT).show()
                        adapter.notifyItemChanged(operationPosition, fileBean)
                    }
                }
            }

        }

    }
}