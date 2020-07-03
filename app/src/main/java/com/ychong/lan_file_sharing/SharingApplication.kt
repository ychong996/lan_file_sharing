package com.ychong.lan_file_sharing

import android.app.Application
import android.util.Log
import com.tencent.smtt.sdk.QbSdk

class SharingApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        X5Init()
    }
    fun  X5Init() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
       val cb =  object :QbSdk.PreInitCallback{
            override fun  onViewInitFinished(arg0:Boolean ) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.e("X5", " onViewInitFinished is " + arg0);
            }
            override fun onCoreInitFinished() {
                Log.e("X5", " onCoreInitFinished   @@@@@@@@@@" );
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }
}