package com.ychong.lan_file_sharing.adapter

import android.app.Activity
import android.content.Intent
import android.os.Environment
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ychong.lan_file_sharing.R
import com.ychong.lan_file_sharing.base.BaseRecyclerAdapter
import com.ychong.lan_file_sharing.common.BaseObserver
import com.ychong.lan_file_sharing.common.CustomException
import com.ychong.lan_file_sharing.data.FileBean
import com.ychong.lan_file_sharing.databinding.ItemFileBinding
import com.ychong.lan_file_sharing.databinding.LayoutListFooterBinding
import com.ychong.lan_file_sharing.databinding.LayoutListHeaderBinding
import com.ychong.lan_file_sharing.ui.SeeFileActivity
import com.ychong.lan_file_sharing.utils.ApkUtils
import com.ychong.lan_file_sharing.utils.FileUtils
import com.ychong.lan_file_sharing.utils.FtpUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File

class FileListAdapter(private val activity: Activity, private val files: MutableList<FileBean>) :
    BaseRecyclerAdapter<FileBean>(files) {
    private val localPath =
        Environment.getExternalStorageDirectory().absolutePath + "/lan_file_sharing/"

    private var popupWindow: PopupWindow = PopupWindow()
    private fun showPopupWindow(view: View, position: Int) {
        val selectLayout = View.inflate(
            activity,
            R.layout.popup_select, null
        )
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
            Observable.create(ObservableOnSubscribe<Boolean> { emitter ->
                val result = FtpUtils.instance.download(item.ftpFile)
                if (result) {
                    emitter.onNext(result)
                } else {
                    emitter.onError(
                        CustomException(
                            "下载文件失败"
                        )
                    )
                }
            }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    item.isDownload = true
                    item.localPath = localPath + item.ftpFile.name
                    notifyItemChanged(position)
                }
        }
        installTv.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
            if (item.ftpFile.name.substring(item.ftpFile.name.lastIndexOf(".")) == ".apk") {
                ApkUtils.instance.installApk(activity, File(item.localPath))
            }
        }
        seeTv.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
            val intent = Intent(
                activity,
                SeeFileActivity::class.java
            )
            intent.putExtra("FILE_LOCAL_PATH", item.localPath)
            activity.startActivity(intent)
        }
        deleteTv.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
            Observable.create(ObservableOnSubscribe<Boolean> { emitter ->
                if (FileUtils.instance.deleteFile(localPath, item.ftpFile.name)
                    && FtpUtils.instance.delete(item.ftpFile.name)
                ) {
                    emitter.onNext(true)
                } else {
                    emitter.onError(
                        CustomException(
                            "删除文件失败"
                        )
                    )
                }
            }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : BaseObserver<Boolean>() {
                    override fun success(t: Boolean) {
                        files.removeAt(position)
                        notifyItemRemoved(position)
                        if (position < files.size) {
                            notifyItemRangeChanged(position, files.size - position)
                        }
                    }

                    override fun error(e: CustomException) {
                        Toast.makeText(activity, e.msg, Toast.LENGTH_SHORT).show()
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

    override fun getItemBinding(parent: ViewGroup): ViewBinding {
        return ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun getHeaderBinding(parent: ViewGroup): ViewBinding {
        return LayoutListHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun getFooterBinding(parent: ViewGroup): ViewBinding {
        return LayoutListFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun convert(holder: ItemViewHolder, position: Int) {
        val item = files[position]
        val binding = holder.binding as ItemFileBinding
        binding.fileNameTv.text = item.ftpFile.name
        if (FileUtils.instance.existsFile(localPath, item.ftpFile.name)) {
            item.isDownload = true
            item.localPath = localPath + item.ftpFile.name
        }
        binding.operationBtn.setOnClickListener {
            showPopupWindow(binding.operationBtn, position)
        }
    }

    override fun convertHeader(holder: HeaderViewHolder) {
        val binding = holder.binding as LayoutListHeaderBinding
        binding.headerLayout.setOnClickListener {
            Toast.makeText(activity, "点击了头部", Toast.LENGTH_SHORT).show()
        }
    }

    override fun convertFooter(holder: FooterViewHolder) {
        val binding = holder.binding as LayoutListFooterBinding
        binding.footerLayout.setOnClickListener {
            Toast.makeText(activity, "点击了尾部", Toast.LENGTH_SHORT).show()
        }
    }
}