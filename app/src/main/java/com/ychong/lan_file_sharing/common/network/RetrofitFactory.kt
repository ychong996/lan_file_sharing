package com.ychong.lan_file_sharing.common.network

import com.ychong.lan_file_sharing.common.BaseConstant
import com.ychong.lan_file_sharing.utils.SSLSocketClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author ychong
 * date: 2020/6/9
 * desc:　
 */

class RetrofitFactory constructor(){
    private val retrofit : Retrofit
    private val interceptor:Interceptor
    private val httpClient:OkHttpClient
    /**
     * 单例
     */
    companion object{
        val instance: RetrofitFactory by lazy { RetrofitFactory() }
    }

    init {
        //通用拦截器
        interceptor = Interceptor {
            chain -> val request = chain.request()
                .newBuilder()
                .build()
            chain.proceed(request)
        }
        //初始化OkHttp
        httpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(10,TimeUnit.SECONDS)
                .connectTimeout(10,TimeUnit.SECONDS)
                .sslSocketFactory(SSLSocketClient.sSLSocketFactory)
                .hostnameVerifier(SSLSocketClient.hostnameVerifier)
                .build()

        //Retrofit实例化
        retrofit = Retrofit.Builder()
                .baseUrl(BaseConstant.HOST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build()
    }

    /**
     * 创建具体服务实例
     */
    fun<T> create(service:Class<T>):T{
        return retrofit.create(service)
    }
}