package com.ychong.lan_file_sharing.utils

import android.content.Context
import android.os.Build

class AppUtils {
    companion object {
        val instance: AppUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { AppUtils() }
    }

    /**
     * 获取版本号
     */
    fun getVersionCode(context: Context):Long{
        val manager = context.packageManager
        var code:Long = 0
        val info = manager.getPackageInfo(context.packageName,0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            code = info.longVersionCode
        }
        return code

    }
    /**
     * 获取版本名
     */
    fun getVersionName(context: Context):String{
        val manager = context.packageManager
        var versionName = "1.0.0.0"
        val info = manager.getPackageInfo(context.packageName,0)
        versionName = info.versionName
        return versionName
    }
}