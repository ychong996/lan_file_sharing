package com.ychong.lan_file_sharing.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.ychong.lan_file_sharing.data.FileBean
import com.ychong.lan_file_sharing.adapter.FileListAdapter
import com.ychong.lan_file_sharing.base.ItemTouchHelperCallback
import com.ychong.lan_file_sharing.databinding.ActivityFileManagerBinding
import com.ychong.lan_file_sharing.utils.FtpUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FileManagerActivity : AppCompatActivity(), OnRefreshListener {
    private lateinit var binding: ActivityFileManagerBinding
    private lateinit var adapter: FileListAdapter
    private var fileList: MutableList<FileBean> = ArrayList<FileBean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        initData()
        initListener()
    }
    private fun initLayout(){
        binding = ActivityFileManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
    private fun initData(){
        binding.headInclude.titleTv.text = "文件管理"

        adapter =
            FileListAdapter(this, fileList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.autoRefresh()

    }
    private fun initListener(){
        binding.refreshLayout.setOnRefreshListener(this)
    }

    @SuppressLint("CheckResult")
    override fun onRefresh(refreshLayout: RefreshLayout) {
        Observable.create(ObservableOnSubscribe<MutableList<FileBean>> { emitter ->
            val files = FtpUtils.instance.getFileList()
            if (!files.isNullOrEmpty()) {
                fileList.clear()
                for (item in files) {
                    fileList.add(FileBean(item))
                }
            }
            emitter.onNext(fileList)
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                binding.refreshLayout.finishRefresh()
                adapter.notifyDataSetChanged()
            }
    }
}