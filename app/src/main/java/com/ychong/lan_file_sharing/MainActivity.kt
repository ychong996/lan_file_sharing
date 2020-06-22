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
    private lateinit var client: FTPClient
    private lateinit var adapter: FileListAdapter
    private var fileList: MutableList<FileBean> = ArrayList<FileBean>()
    private val localPath = Environment.getExternalStorageDirectory().absolutePath
    private var operationPosition=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            Thread(Runnable {
                client = FTPClient()

                val host = binding.ftpIpEt.text.toString()
                val port = binding.ftpPortEt.text.toString().toInt()
                val account = binding.accountEt.text.toString()
                val password = binding.passwordEt.text.toString()
                client.connect(host, port)
                Log.e("FTP ", "连接成功，准备登录")
                client.login(account, password)
                Log.e("FTP ", "登录成功，开始获取数据")
                val files = client.listNames()
                if (!files.isNullOrEmpty()) {
                    val message = Message.obtain()
                    message.what = 0
                    message.obj = files
                    handler.sendMessage(message)

                }
            }).start()
        }
        adapter = FileListAdapter(fileList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.recyclerView.adapter = adapter
        adapter.setFileItemClickListener(object : FileListAdapter.FileItemClickListener {
            override fun onClick(file: FileBean) {
                operationPosition = fileList.indexOf(file);
                Thread(Runnable {
                    val result = downFile(
                        binding.ftpIpEt.text.toString(),
                        binding.ftpPortEt.text.toString().toInt(),
                        binding.accountEt.text.toString(),
                        binding.passwordEt.text.toString(),
                        "/", file.fileName, localPath
                    )

                    val message = Message.obtain()
                    message.what = 1
                    val fileBean = FileBean(file.fileName,result)
                    message.obj = fileBean
                    handler.sendMessage(message)
                }).start()

            }

        })

    }

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                0 ->{
                    val files = msg.obj as Array<String>
                    for (item in files){
                        fileList.add(FileBean(item))
                    }
                    adapter.notifyDataSetChanged()
                }
                1->{
                    val fileBean = msg.obj as FileBean
                    if (fileBean.isDownload){
                        Toast.makeText(this@MainActivity,"下载成功",Toast.LENGTH_SHORT).show()
                        adapter.notifyItemChanged(operationPosition,fileBean)
                    }
                }
            }

        }

    }

    fun downFile(
        url: String?, port: Int, username: String?,
        password: String?, remotePath: String, fileName: String,
        localPath: String
    ): Boolean {
        var result = false
        try {
            client.controlEncoding = System.getProperty("file.encoding")
            if (!client.isConnected) {
                client.connect(url, port)
                // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
                client.login(username, password) // 登录
            }

            // 设置文件传输类型为二进制
            client.setFileType(FTPClient.BINARY_FILE_TYPE)
            // 获取ftp登录应答代码
            val reply: Int = client.replyCode
            // 验证是否登陆成功
            if (!FTPReply.isPositiveCompletion(reply)) {
                client.disconnect()
                System.err.println("FTP server refused connection.")
                return result
            }
            // 转移到FTP服务器目录至指定的目录下
            client.changeWorkingDirectory(
                String(
                    remotePath.toByteArray(Charset.forName("UTF-8")),
                    Charset.forName("iso-8859-1")
                )
            )
            // 获取文件列表
            val fs: Array<FTPFile> = client.listFiles()
            for (ff in fs) {
                if (ff.name == fileName) {
                    val localFile = File(localPath + "/" + ff.name)

                    val `is`: OutputStream = FileOutputStream(localFile)
                    client.retrieveFile(ff.name, `is`)
                    `is`.close()
                }
            }
            client.logout()
            result = true
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (client.isConnected) {
                try {
                    client.disconnect()
                } catch (ioe: IOException) {
                }
            }
        }
        return result
    }
}