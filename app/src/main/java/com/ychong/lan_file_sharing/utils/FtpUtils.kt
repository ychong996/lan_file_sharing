package com.ychong.lan_file_sharing.utils

import android.os.Environment
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.nio.charset.Charset

class FtpUtils {
    private var client: FTPClient? = null
    private lateinit var account: String
    private lateinit var password: String
    private lateinit var host: String
    private var port: Int = 0
    private val localPath = Environment.getExternalStorageDirectory().absolutePath+"/lan_file_sharing/"

    companion object {
        val instance: FtpUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { FtpUtils() }
    }

    init {
        client = FTPClient()
    }

    /**
     * 连接
     */
    fun connect(host: String, port: Int): Boolean {
        try {
            if (!client!!.isConnected) {
                this.host = host
                this.port = port
                client!!.connect(this.host, this.port)
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 登陆
     */
    fun login(account: String, password: String): Boolean {
        try {
            this.account = account
            this.password = password
            client!!.login(this.account, this.password)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getFileList(): Array<out FTPFile>? {
        client!!.enterLocalPassiveMode()
        return client!!.listFiles()
    }

    fun download(targetFile: FTPFile): Boolean {
        var result = false
        if (FileUtils.instance.existsFile(localPath,targetFile.name)){
            result = true
            return result
        }
        try {
            client!!.controlEncoding = System.getProperty("file.encoding")
            if (!client!!.isConnected) {
                client!!.connect(this.host, this.port)
                // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
                client!!.login(this.account, this.password) // 登录
            }

            // 设置文件传输类型为二进制
            client!!.setFileType(FTPClient.BINARY_FILE_TYPE)
            // 获取ftp登录应答代码
            val reply: Int = client!!.replyCode
            // 验证是否登陆成功
            if (!FTPReply.isPositiveCompletion(reply)) {
                client!!.disconnect()
                System.err.println("FTP server refused connection.")
                return result
            }
            val remotePath = "/"
            // 转移到FTP服务器目录至指定的目录下
            client!!.changeWorkingDirectory(
                String(
                    remotePath.toByteArray(Charset.forName("UTF-8")),
                    Charset.forName("iso-8859-1")
                )
            )
            val dirFile = File(localPath)
            if (!dirFile.exists()){
                dirFile.mkdir()
            }
            val localFile = File(localPath + targetFile.name)

            val `is`: OutputStream = FileOutputStream(localFile)
            client!!.retrieveFile(targetFile.name, `is`)
            `is`.close()
            result = true
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (client!!.isConnected) {
                try {
                    client!!.disconnect()
                } catch (ioe: IOException) {
                }
            }
        }
        return result
    }

    fun delete(fileName:String):Boolean{
        val result = false
        try {
        client!!.controlEncoding = System.getProperty("file.encoding")
        if (!client!!.isConnected) {
            client!!.connect(this.host, this.port)
            // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            client!!.login(this.account, this.password) // 登录
        }

        // 设置文件传输类型为二进制
        client!!.setFileType(FTPClient.BINARY_FILE_TYPE)
        // 获取ftp登录应答代码
        val reply: Int = client!!.replyCode
        // 验证是否登陆成功
        if (!FTPReply.isPositiveCompletion(reply)) {
            client!!.disconnect()
            System.err.println("FTP server refused connection.")
            return result
        }
        val remotePath = "/"
        // 转移到FTP服务器目录至指定的目录下
        client!!.changeWorkingDirectory(
            String(
                remotePath.toByteArray(Charset.forName("UTF-8")),
                Charset.forName("iso-8859-1")
            )
        )
        return client!!.deleteFile(fileName)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return false
    }

    fun destroy() {
        client!!.logout()
        client = null
    }
}