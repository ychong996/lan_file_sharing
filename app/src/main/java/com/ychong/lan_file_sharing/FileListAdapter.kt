package com.ychong.lan_file_sharing

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ychong.lan_file_sharing.databinding.ItemFileBinding

class FileListAdapter(private val files: List<FileBean>) :
    RecyclerView.Adapter<FileListAdapter.FileListViewHolder>() {
    class FileListViewHolder(val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root)

    private lateinit var fileItemClickListener: FileItemClickListener
    fun setFileItemClickListener(fileItemClickListener: FileItemClickListener) {
        this.fileItemClickListener = fileItemClickListener
    }

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
        holder.binding.fileNameTv.text = item.fileName
        Log.e("文件类型",item.fileName.substring(item.fileName.lastIndexOf(".")))
        if (item.isDownload){
            if (item.fileName.substring(item.fileName.lastIndexOf(".")) == ".apk"){
                holder.binding.operationBtn.text = "安装"
            }else{
                holder.binding.operationBtn.text = "查看"
            }

        }else{
            holder.binding.operationBtn.text = "下载"
        }
        holder.binding.operationBtn.setOnClickListener { fileItemClickListener.onClick(item) }
    }

    interface FileItemClickListener {
        fun onClick(file: FileBean)
    }
}