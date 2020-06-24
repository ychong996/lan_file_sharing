package com.ychong.lan_file_sharing

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.widget.PopupWindow

class CustomPopupWindow() : PopupWindow(),View.OnClickListener, Parcelable {

    constructor(context:Context) : this() {
        initLayout(context)
    }
    private fun initLayout(context: Context){

    }
    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }

}