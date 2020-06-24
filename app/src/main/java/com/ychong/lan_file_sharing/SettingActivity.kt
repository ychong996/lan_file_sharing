package com.ychong.lan_file_sharing

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ychong.lan_file_sharing.databinding.ActivitySettingBinding

class SettingActivity:AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        initData()
        initListener()
    }

    private fun initListener() {
        binding.headInclude.leftTv.setOnClickListener(this)
        binding.headInclude.rightTv.setOnClickListener(this)
    }

    private fun initData() {
        binding.headInclude.titleTv.text = "设置"
        binding.headInclude.leftTv.visibility = View.VISIBLE
        binding.headInclude.rightTv.visibility = View.VISIBLE
        binding.headInclude.rightTv.text = "保存"
    }

    private fun initLayout() {
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.leftTv ->{
                onBackPressed()
            }
            R.id.rightTv ->{
                //保存
                save()
                onBackPressed()
            }
        }
    }

    private fun save(){
        SPUtils.getInstance(this).putString("FTP_IP",binding.ftpIpEt.text.toString())
        SPUtils.getInstance(this).putInt("FTP_PORT",binding.ftpPortEt.text.toString().toInt())
    }
}