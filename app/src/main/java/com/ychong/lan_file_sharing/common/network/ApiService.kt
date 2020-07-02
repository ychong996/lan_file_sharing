package com.ychong.lan_file_sharing.common.network

import com.ychong.lan_file_sharing.data.BaseResp
import com.ychong.lan_file_sharing.data.FileBean
import com.ychong.lan_file_sharing.data.LoginResp
import com.ychong.lan_file_sharing.data.MenuBean
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.*

interface ApiService {
    /**
     * 设置ftp地址和端口号
     */
    @POST
    fun setFTPAddress(@Body body: RequestBody)

    /**
     * 登陆
     */
    @POST("/User/login")
    fun login(@Body body: RequestBody):Observable<BaseResp<LoginResp>>

    /**
     * 获取菜单数据
     */
    @GET("/Menu/getMenuList")
    fun getMenuList():Observable<BaseResp<MutableList<MenuBean>>>


    @GET("/File/getFileList")
    fun getFileList():Observable<BaseResp<MutableList<FileBean>>>

}