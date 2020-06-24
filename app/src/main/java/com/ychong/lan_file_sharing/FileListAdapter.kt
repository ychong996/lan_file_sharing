package com.ychong.lan_file_sharing

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.*
import android.widget.GridView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ychong.lan_file_sharing.databinding.ItemFileBinding
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class FileListAdapter(private val activity: Activity, private val files: MutableList<FileBean>) :
    RecyclerView.Adapter<FileListAdapter.FileListViewHolder>() {
    class FileListViewHolder(val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root)

    private val localPath =
        Environment.getExternalStorageDirectory().absolutePath + "/lan_file_sharing/"

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
        if (FileUtils.instance.existsFile(localPath, item.ftpFile.name)) {
            item.isDownload = true
            item.localPath = localPath + item.ftpFile.name
        }
        holder.binding.operationBtn.setOnClickListener {
            showPopupWindow(holder.binding.operationBtn,position)
        }
    }

    private var popupWindow: PopupWindow = PopupWindow()
    private fun showPopupWindow(view: View,position: Int) {
        val selectLayout = View.inflate(activity, R.layout.popup_select, null)
        val downloadTv = selectLayout.findViewById<TextView>(R.id.downloadTv)
        val installTv = selectLayout.findViewById<TextView>(R.id.installTv)
        val seeTv = selectLayout.findViewById<TextView>(R.id.seeTv)
        val deleteTv = selectLayout.findViewById<TextView>(R.id.deleteTv)
        selectLayout.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
        }
        val item = files[position]
        if (item.isDownload) {
            if (item.ftpFile.name.substring(item.ftpFile.name.lastIndexOf(".")) == ".apk") {
                installTv.visibility = View.VISIBLE
            } else {
                seeTv.visibility = View.VISIBLE
            }
            deleteTv.visibility = View.VISIBLE
        } else {
           downloadTv.visibility = View.VISIBLE
        }
        downloadTv.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
            Observable.create(ObservableOnSubscribe<Boolean> {emitter ->
                val result = FtpUtils.instance.download(item.ftpFile)
                if (result){
                    emitter.onNext(result)
                }else{
                    emitter.onError(CustomException("下载文件失败"))
                }
            }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    item.isDownload = true
                    item.localPath = localPath+item.ftpFile.name
                    notifyItemChanged(position)
                }
        }
        installTv.setOnClickListener{
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
            if (item.ftpFile.name.substring(item.ftpFile.name.lastIndexOf(".")) == ".apk"){
                ApkUtils.instance.installApk(activity,File(item.localPath))
            }
        }
        seeTv.setOnClickListener{
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
            val intent = Intent(activity,SeeFileActivity::class.java)
            intent.putExtra("FILE_LOCAL_PATH",item.localPath)
            activity.startActivity(intent)
        }
        deleteTv.setOnClickListener{
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
            Observable.create(ObservableOnSubscribe<Boolean> {emitter ->
                if (FileUtils.instance.deleteFile(localPath,item.ftpFile.name)
                    &&FtpUtils.instance.delete(item.ftpFile.name)){
                    emitter.onNext(true)
                }else{
                    emitter.onError(CustomException("删除文件失败"))
                }
            }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : BaseObserver<Boolean>(){
                    override fun success(t: Boolean) {
                        files.removeAt(position)
                        notifyItemRemoved(position)
                        if (position<files.size){
                            notifyItemRangeChanged(position,files.size-position)
                        }
                    }

                    override fun error(e: CustomException) {
                        Toast.makeText(activity,e.msg,Toast.LENGTH_SHORT).show()
                    }

                })
        }
        popupWindow.contentView = selectLayout
        popupWindow.width = view.width
        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.isTouchable = true
        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.showAsDropDown(view)
    }
}