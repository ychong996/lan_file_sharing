package com.ychong.lan_file_sharing

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaCodec.MetricsConstants.MODE

class SPUtils{
    private var sp: SharedPreferences? = null
    private var edit: SharedPreferences.Editor?= null

    constructor(context: Context){
        initSP(context.applicationContext)
    }
    @SuppressLint("CommitPrefEdits")
    private fun initSP(context: Context){
        sp = context.getSharedPreferences("config",Context.MODE_PRIVATE)
    }
    companion object{
        @Volatile
        private var instance:SPUtils?=null
        fun getInstance(context: Context):SPUtils{
            if (instance == null){
                synchronized(SPUtils::class){
                    if (instance == null){
                        instance = SPUtils(context)
                    }
                }
            }
            return instance!!
        }
    }
    @SuppressLint("CommitPrefEdits")
    public fun put(key:String, value:Any){
        edit = sp!!.edit()


    }
    @SuppressLint("CommitPrefEdits")
    public fun putString(key:String, value:String){
        edit = sp!!.edit()
        edit!!.putString(key,value)
        edit!!.apply()
    }

    @SuppressLint("CommitPrefEdits")
    public fun putInt(key:String, value:Int){
        edit = sp!!.edit()
        edit!!.putInt(key,value)
        edit!!.apply()
    }

    fun getString(key:String): String? {
        return sp!!.getString(key,"")
    }
    fun getInt(key:String):Int{
        return sp!!.getInt(key,-1)
    }

}