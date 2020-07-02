package com.ychong.lan_file_sharing.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.ychong.lan_file_sharing.data.FileBean
import com.ychong.lan_file_sharing.adapter.FileListAdapter
import com.ychong.lan_file_sharing.base.ItemTouchHelperCallback
import com.ychong.lan_file_sharing.common.BaseConstant
import com.ychong.lan_file_sharing.common.CustomException
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
import java.io.File

class FileManagerActivity : AppCompatActivity(), OnRefreshListener {
    private var ftpPassword: String? = null
    private var ftpAccount: String? = null
    private var port: Int = -1
    private var host: String? = null
    private lateinit var binding: ActivityFileManagerBinding
    private lateinit var adapter: FileListAdapter
    private var fileList: MutableList<FileBean> = ArrayList<FileBean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        initData()
        initListener()
    }

    private fun initLayout() {
        binding = ActivityFileManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    @SuppressLint("CheckResult")
    private fun initData() {
        binding.headInclude.titleTv.text = "文件管理"
        adapter =
            FileListAdapter(this, fileList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.autoRefresh()

    }

    private fun initListener() {
        binding.refreshLayout.setOnRefreshListener(this)
    }

    private fun checkData(): Boolean {
        ftpAccount = SPUtils.getInstance(this).getString(BaseConstant.SP_FTP_ACCOUNT)
        ftpPassword = SPUtils.getInstance(this).getString(BaseConstant.SP_FTP_PASSWORD)
        host = SPUtils.getInstance(this).getString(BaseConstant.SP_FTP_IP)
        port = SPUtils.getInstance(this).getInt(BaseConstant.SP_FTP_PORT)

        if (host.isNullOrEmpty()) {
            Toast.makeText(this, "FTP地址有误，请检查", Toast.LENGTH_SHORT).show()
            return false
        }
        if (port == -1) {
            Toast.makeText(this, "FTP端口有误，请检查", Toast.LENGTH_SHORT).show()
            return false
        }
        if (ftpAccount.isNullOrEmpty()) {
            Toast.makeText(this, "账号有误，请检查", Toast.LENGTH_SHORT).show()
            return false
        }
        if (ftpPassword.isNullOrEmpty()) {
            Toast.makeText(this, "密码有误，请检查", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    @SuppressLint("CheckResult")
    override fun onRefresh(refreshLayout: RefreshLayout) {
        adapter.clearData()
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
                    Log.e("文件列表返回数据 ",t.toString())
                    if (t.success) {
                        val list = t.resultBody
                        adapter.setData(list)
                    }
                }

                override fun error(e: Throwable) {

                }
            })
    }
}