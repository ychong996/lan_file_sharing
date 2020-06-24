package com.ychong.lan_file_sharing

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.ychong.lan_file_sharing.databinding.PopupSelectBinding

class CustomPopupWindow() : PopupWindow(),View.OnClickListener {
    private lateinit var binding:PopupSelectBinding
    private lateinit var activity: Activity
    constructor( activity: Activity) : this() {
        initLayout(activity)
        setPopupWindow()
        initListener()
    }
    private fun initLayout(activity: Activity){
        this.activity = activity
        binding = PopupSelectBinding.inflate(LayoutInflater.from(activity),null,false)
        contentView = binding.root

    }
    private fun setPopupWindow(){
        this.height = ViewGroup.LayoutParams.MATCH_PARENT
        this.isFocusable = true
        //this.setBackgroundDrawable(ColorDrawable(0x00000000))
        this.isOutsideTouchable = true
        this.setOnDismissListener {
            val lp = activity.window.attributes
            lp.alpha = 1.0f
            activity.window.attributes = lp
        }
    }
    private fun initListener(){
        binding.downloadTv.setOnClickListener(this)
        binding.installTv.setOnClickListener(this)
        binding.seeTv.setOnClickListener(this)
    }
    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.downloadTv ->{

            }
            R.id.installTv ->{

            }
            R.id.seeTv ->{

            }
        }
    }



}