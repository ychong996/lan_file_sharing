package com.ychong.lan_file_sharing.utils

import android.os.Environment
import android.util.Log
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply
import java.io.*
import java.lang.Exception
import java.nio.charset.Charset

class FtpUtils {
    private var client: FTPClient? = null
    private lateinit var account: String
    private lateinit var password: String
    private lateinit var host: String
    private var port: Int = 0
    private val localPath =
        Environment.getExternalStorageDirectory().absolutePath + "/lan_file_sharing/"

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
                client!!.autodetectUTF8 = true
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

    fun download(targetFileName: String): Boolean {
        var result = false
        if (FileUtils.instance.existsFile(localPath, targetFileName)) {
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
            client!!.controlEncoding = "UTF-8"
            // 转移到FTP服务器目录至指定的目录下
            client!!.changeWorkingDirectory(
                String(
                    remotePath.toByteArray(Charset.forName("UTF-8")),
                    Charset.forName("iso-8859-1")
                )
            )
            val dirFile = File(localPath)
            if (!dirFile.exists()) {
                dirFile.mkdir()
            }
            val localFile = File(localPath + targetFileName)

            val fos = FileOutputStream(localFile)
            val retrieve = client!!.retrieveFile(targetFileName, fos)
            fos.close()
            result = retrieve
        } catch (e: IOException) {
            e.printStackTrace()
            result = false
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

    fun delete(fileName: String): Boolean {
        val result = false
        try {
            if (client==null){
                client = FTPClient()
            }
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 上传文件
     * @param pathname ftp服务保存地址
     * @param fileName 上传到ftp的文件名
     *  @param originfilename 待上传文件的名称（绝对地址） *
     * @return
     */
    fun uploadFile(fileName:String, localFilePath:String):Boolean{
        var flag = false;
        var inputStream: FileInputStream? = null
        try{
            println("开始上传文件");
            inputStream =  FileInputStream(File(localFilePath))
            client!!.setFileType(FTPClient.BINARY_FILE_TYPE);
            client!!.makeDirectory("/");
            client!!.changeWorkingDirectory("/");
            client!!.storeFile(fileName, inputStream);
            inputStream.close();
            client!!.logout();
            flag = true;
            println("上传文件成功");
        }catch (e:Exception) {
            println("上传文件失败");
            e.printStackTrace();
        }finally{
            if(client!!.isConnected){
                try{
                    client!!.disconnect();
                }catch(e:IOException){
                    e.printStackTrace();
                }
            }
            if(null != inputStream){
                try {
                    inputStream.close();
                } catch (e:IOException) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    fun destroy() {
        if (client != null && client!!.isAvailable) {
            client!!.logout()
            client = null
        }

    }
}