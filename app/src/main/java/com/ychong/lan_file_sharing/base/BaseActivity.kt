package com.ychong.lan_file_sharing.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.ychong.lan_file_sharing.R

open class BaseActivity:AppCompatActivity() ,IBaseActivity{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .reset()
            .statusBarColor(R.color.md_white_1000)
            .statusBarDarkFont(true)
            .fitsSystemWindows(true)
            .init()
        initLayout()
        initData()
        initListener()
    }

    override fun initLayout() {
    }

    override fun initData() {

    }

    override fun initListener() {

    }
}