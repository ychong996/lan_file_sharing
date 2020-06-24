package com.ychong.lan_file_sharing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File


class ApkUtils {
    companion object {
        val instance: ApkUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { ApkUtils() }
    }
    /**
     * 安装Apk
     */
    open fun installApk(activity: Activity, apkFile: File?): Unit {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //Uri uri = Uri.fromFile(apkFile);
        var uri: Uri? = null
        //todo N FileProvider
        //todo O install permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(
                activity,
                activity.packageName+".fileProvider",
                apkFile!!
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            uri = Uri.fromFile(apkFile)
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        activity.startActivity(intent)
    }

}