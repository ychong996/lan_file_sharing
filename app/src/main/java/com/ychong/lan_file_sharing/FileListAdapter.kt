package com.ychong.lan_file_sharing

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ychong.lan_file_sharing.databinding.ItemFileBinding
import java.io.File

class FileListAdapter(private val activity: Activity, private val files: List<FileBean>) :
    RecyclerView.Adapter<FileListAdapter.FileListViewHolder>() {
    class FileListViewHolder(val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root)
    private val localPath = Environment.getExternalStorageDirectory().absolutePath+"/lan_file_sharing/"
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FileListViewHolder {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (files.isNullOrEmpty()) 0 else files.size
    }

    override fun onBindViewHolder(holder: FileListViewHolder, position: Int) {
        val item = files[position]
        holder.binding.fileNameTv.text = item.ftpFile.name
        if (FileUtils.instance.existsFile(localPath,item.ftpFile.name)){
            item.isDownload = true
            item.localPath = localPath+item.ftpFile.name
        }
        Log.e("文件类型",item.ftpFile.name.substring(item.ftpFile.name.lastIndexOf(".")))
        if (item.isDownload){
            if (item.ftpFile.name.substring(item.ftpFile.name.lastIndexOf(".")) == ".apk"){
                holder.binding.operationBtn.text = "安装"
            }else{
                holder.binding.operationBtn.text = "查看"
            }

        }else{
            holder.binding.operationBtn.text = "下载"
        }
        holder.binding.operationBtn.setOnClickListener {
            if (item.isDownload){
                if (item.ftpFile.name.substring(item.ftpFile.name.lastIndexOf(".")) == ".apk"){
                   ApkUtils.instance.installApk(activity,File(item.localPath))
                }else{
                    holder.binding.operationBtn.text = "查看"
                }
            }else{
                Thread(Runnable {
                    val result = FtpUtils.instance.download(item.ftpFile)
                    if (result){
                        item.isDownload = result
                        item.localPath = localPath+item.ftpFile.name
                        val msg = Message.obtain()
                        msg.arg1 = position
                        handler.sendMessage(msg)
                    }
                }).start()
            }

        }
    }

    val handler = @SuppressLint("HandlerLeak")
    object :Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            notifyItemChanged(msg.arg1)
        }
    }
}