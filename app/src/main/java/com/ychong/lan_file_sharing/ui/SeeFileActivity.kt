package com.ychong.lan_file_sharing.ui

import android.R.attr.path
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.tencent.smtt.sdk.TbsReaderView
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebView
import com.ychong.lan_file_sharing.common.BaseConstant
import com.ychong.lan_file_sharing.databinding.ActivitySeeFileBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*


class SeeFileActivity : AppCompatActivity(), PreInitCallback,
    ValueCallback<String>, TbsReaderView.ReaderCallback {
    private lateinit var readerView: TbsReaderView
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

    /**
     * 打开文件阅读器
     */
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
        //通过bundle把文件传给x5,打开的事情交由x5处理

        //通过bundle把文件传给x5,打开的事情交由x5处理
        val bundle = Bundle()
        //传递文件路径
        //传递文件路径
        bundle.putString("filePath", filePath)
        //临时的路径
        //临时的路径
        bundle.putString("tempPath", tempPath)
        readerView = TbsReaderView(this,
            TbsReaderView.ReaderCallback { integer: Int?, o: Any?, o1: Any? -> }
        )
        //加载文件前的初始化工作,加载支持不同格式的插件
        //加载文件前的初始化工作,加载支持不同格式的插件
        val b = readerView.preOpen(getFileType(filePath), true)
        if (b) {
            readerView.openFile(bundle)
        }else{
            Log.e("TAG","加载文件失败")
        }
        // 往容器里添加TbsReaderView控件
        // 往容器里添加TbsReaderView控件
        binding.wvLayout.addView(readerView)
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

    override fun onDestroy() {
        val parent = binding.webView.parent as ViewGroup
        parent.removeView(webView)
        binding.webView.clearHistory()
        binding.webView.clearCache(true)
        binding.webView.destroy()

        readerView.onStop()

        super.onDestroy()
    }
}