package com.ychong.lan_file_sharing.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.ychong.lan_file_sharing.R
import com.ychong.lan_file_sharing.adapter.MainRecyclerAdapter
import com.ychong.lan_file_sharing.common.network.ApiService
import com.ychong.lan_file_sharing.common.network.BaseObserver
import com.ychong.lan_file_sharing.common.network.RetrofitFactory
import com.ychong.lan_file_sharing.data.BaseResp
import com.ychong.lan_file_sharing.data.MenuBean
import com.ychong.lan_file_sharing.databinding.ActivityMainBinding
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

class MainActivity : AppCompatActivity(), OnRefreshListener {
    private lateinit var adapter: MainRecyclerAdapter
    private lateinit var binding: ActivityMainBinding
    private var menuList: MutableList<MenuBean> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        initData()
        initListener()
    }

    private fun initLayout() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    private fun initData() {
        adapter = MainRecyclerAdapter(menuList)
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.adapter = adapter
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.autoRefresh()

    }

    private fun initListener() {
        adapter.setItemClickListener(object : MainRecyclerAdapter.ItemClickListener {
            override fun onClick(item: MenuBean) {
                when (item.id) {
                    0 -> {
                        //FTP
                        startActivity(Intent(this@MainActivity, FileManagerActivity::class.java))
                    }
                }

            }
        })
    }

    private fun getMenuList() {
        RetrofitFactory.instance.create(ApiService::class.java)
            .getMenuList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<BaseResp<MutableList<MenuBean>>>() {
                override fun success(t: BaseResp<MutableList<MenuBean>>) {
                    if (t.success) {
                        val list = t.resultBody
                        adapter.setData(list)
                    }
                }

                override fun error(e: Throwable) {

                }
            })
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        adapter.clearData()
        getMenuList()
        refreshLayout.finishRefresh(300)
    }
}