package com.ychong.lan_file_sharing.ui

import android.R.attr.path
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.tencent.smtt.sdk.TbsReaderView
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebView
import com.ychong.lan_file_sharing.R
import com.ychong.lan_file_sharing.base.BaseActivity
import com.ychong.lan_file_sharing.common.BaseConstant
import com.ychong.lan_file_sharing.databinding.ActivitySeeFileBinding
import kotlinx.android.synthetic.main.layout_head.view.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*


class SeeFileActivity : BaseActivity(), PreInitCallback,
    ValueCallback<String>, TbsReaderView.ReaderCallback, View.OnClickListener {
    private lateinit var readerView: TbsReaderView
    private val tempPath = Environment.getExternalStorageDirectory().absolutePath + "/lan_file_sharing/temp"
    private lateinit var webView: WebView
    private lateinit var binding: ActivitySeeFileBinding

    override fun initLayout() {
        binding = ActivitySeeFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

     override fun initData() {
        binding.headerInclude.titleTv.text = "查看文件"
        binding.headerInclude.leftTv.visibility = View.VISIBLE
        readerView = TbsReaderView(this, this)
        binding.wvLayout.addView(readerView)
        webView = WebView(this)
        val filePath = intent.getStringExtra("FILE_LOCAL_PATH")
        if (filePath.isNullOrEmpty()) {
            Toast.makeText(this, "文件路径不存在", Toast.LENGTH_SHORT).show()
            return
        }
        val file = File(filePath)
        if (!file.exists()) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show()
            return
        }
        when (filePath.substring(filePath.lastIndexOf("."))) {
            ".txt", ".doc", ".docx", ".ppt", ".pptx", ".xls", ".xlsx" -> {
                openFile(filePath)
            }
            ".apk" -> {

            }
        }
    }

     override fun initListener() {
        binding.headerInclude.leftTv.setOnClickListener(this)
    }

    override fun onCoreInitFinished() {
    }

    override fun onViewInitFinished(p0: Boolean) {
    }

    override fun onReceiveValue(p0: String?) {
    }

    private fun openFile(filePath: String) {
        val mFile = File(filePath)
        if (!TextUtils.isEmpty(mFile.toString())) {
            //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
            val bsReaderTempFile = File(tempPath);

            if (!bsReaderTempFile.exists()) {
                val mkdir = bsReaderTempFile.mkdir();
                if (!mkdir) {
                }
            }

            //加载文件
            val localBundle = Bundle();
            localBundle.putString("filePath", mFile.toString());

            localBundle.putString("tempPath", tempPath)

            val bool = this.readerView.preOpen(getFileType(mFile.toString()), false);
            if (bool) {
                this.readerView.openFile(localBundle);
            }
        } else {
            Log.e("", "文件路径无效！");
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
        Log.e("文件类型后缀 ", typeTag)
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

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.leftTv -> {
                onBackPressed()
            }
        }
    }
}