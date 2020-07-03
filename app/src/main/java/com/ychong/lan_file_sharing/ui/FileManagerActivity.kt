package com.ychong.lan_file_sharing.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.ychong.lan_file_sharing.R
import com.ychong.lan_file_sharing.data.FileBean
import com.ychong.lan_file_sharing.adapter.FileListAdapter
import com.ychong.lan_file_sharing.base.BaseActivity
import com.ychong.lan_file_sharing.base.ItemTouchHelperCallback
import com.ychong.lan_file_sharing.common.BaseConstant
import com.ychong.lan_file_sharing.common.CustomException
import com.ychong.lan_file_sharing.common.EventMsg
import com.ychong.lan_file_sharing.common.network.ApiService
import com.ychong.lan_file_sharing.common.network.BaseObserver
import com.ychong.lan_file_sharing.common.network.RetrofitFactory
import com.ychong.lan_file_sharing.data.BaseResp
import com.ychong.lan_file_sharing.databinding.ActivityFileManagerBinding
import com.ychong.lan_file_sharing.utils.FtpUtils
import com.ychong.lan_file_sharing.utils.SPUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.layout_head.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

class FileManagerActivity : BaseActivity(), OnRefreshListener, View.OnClickListener {
    private lateinit var binding: ActivityFileManagerBinding
    private lateinit var adapter: FileListAdapter
    private var fileList: MutableList<FileBean> = ArrayList()

     override fun initLayout() {
        binding = ActivityFileManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    @SuppressLint("CheckResult")
     override fun initData() {
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
        binding.headInclude.titleTv.text = "文件管理"
        binding.headInclude.rightTv.visibility = View.VISIBLE
        binding.headInclude.rightTv.text = "添加"
        adapter =
            FileListAdapter(this, fileList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.autoRefresh()

    }

     override fun initListener() {
         binding.headInclude.rightTv.setOnClickListener(this)
        binding.refreshLayout.setOnRefreshListener(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(eventMsg: EventMsg){
        val type = eventMsg.type
        if (type == "refresh"){
            val msg = eventMsg.msg
            if (msg == "fileList"){
                adapter.notifyDataSetChanged()
            }
        }
    }
    @SuppressLint("CheckResult")
    override fun onRefresh(refreshLayout: RefreshLayout) {
        //adapter.clearData()
        getFileList()
        refreshLayout.finishRefresh(300)
    }

    private fun getFileList() {
        RetrofitFactory.instance.create(ApiService::class.java)
            .getFileList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<BaseResp<MutableList<FileBean>>>() {
                override fun success(t: BaseResp<MutableList<FileBean>>) {
                    if (t.success) {
                        val list = t.resultBody
                        adapter.setData(list)
                    }
                }

                override fun error(e: Throwable) {

                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.rightTv ->{
                startActivity(Intent(this,AddFileActivity::class.java))
            }
        }
    }
}