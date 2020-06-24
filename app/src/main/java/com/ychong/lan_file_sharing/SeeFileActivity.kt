package com.ychong.lan_file_sharing

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.tencent.smtt.sdk.TbsReaderView
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebView
import com.ychong.lan_file_sharing.databinding.ActivitySeeFileBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*


class SeeFileActivity : AppCompatActivity(), PreInitCallback,
    ValueCallback<String>, TbsReaderView.ReaderCallback {
    private val tempPath = Environment.getExternalStorageDirectory().absolutePath+"/lan_file_sharing/temp"
    private lateinit var webView: WebView
    private lateinit var binding: ActivitySeeFileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        initData()
        initListener()
    }
    private fun initLayout(){
        binding = ActivitySeeFileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    private fun initData(){
        QbSdk.forceSysWebView()
        QbSdk.initX5Environment(this, this)
        webView = WebView(this)
        val filePath = intent.getStringExtra("FILE_LOCAL_PATH")
        if (filePath.isNullOrEmpty()){
            Toast.makeText(this,"文件路径不存在",Toast.LENGTH_SHORT).show()
            return
        }
        val file = File(filePath)
        if (!file.exists()){
            Toast.makeText(this,"文件不存在",Toast.LENGTH_SHORT).show()
            return
        }
        when(filePath.substring(filePath.lastIndexOf("."))){
            ".txt" ->{
                openFile(this,filePath)
            }
            ".apk" ->{

            }
        }
    }
    private fun initListener(){

    }

    override fun onCoreInitFinished() {
    }

    override fun onViewInitFinished(p0: Boolean) {
    }

    override fun onReceiveValue(p0: String?) {
    }
    private fun openFileReader(
        context: Context,
        pathName: String?
    ) {
        val params =
            HashMap<String, String>()
        params["local"] = "false"
        val Object = JSONObject()
        try {
            Object.put("pkgName", context.applicationContext.packageName)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        params["menuData"] = Object.toString()
        QbSdk.getMiniQBVersion(context)
        val ret = QbSdk.openFileReader(context, pathName, params, this)
    }

    private fun openFile(activity:Activity,filePath:String){
        val readerView = TbsReaderView(this,this)
        binding.wvLayout.addView(readerView,LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT))

        val bundle = Bundle()
        bundle.putString("filePath",filePath)
        bundle.putString("tempPath",tempPath)
        val isOpen = readerView.preOpen(getFileType(filePath),false)
        if (isOpen){
            readerView.openFile(bundle)
        }else{
            openFileReader(activity,filePath)
        }
    }

    override fun onCallBackAction(p0: Int?, p1: Any?, p2: Any?) {

    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private fun getFileType(paramString: String): String? {
        var typeTag: String? = ""
        if (TextUtils.isEmpty(paramString)) {
            return typeTag
        }
        val i = paramString.lastIndexOf('.')
        if (i <= -1) {
            return typeTag
        }
        typeTag = paramString.substring(i + 1)
        Log.e("文件类型后缀 ",typeTag)
        return typeTag
    }

}