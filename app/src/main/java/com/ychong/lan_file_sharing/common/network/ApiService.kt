package com.ychong.lan_file_sharing.common.network

import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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
    fun login(@Body body: RequestBody):Observable<ResponseBody>

    /**
     * 获取菜单数据
     */
    @GET("/Menu/getMenuList")
    fun getMenuList():Observable<ResponseBody>
}