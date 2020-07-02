package com.ychong.lan_file_sharing.utils

import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * @author jingwei
 * date: 2019/8/12
 * desc: 忽略ssl验证
 */
object SSLSocketClient {
    //获取这个SSLSocketFactory
    val sSLSocketFactory: SSLSocketFactory
        get() = try {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustManager, SecureRandom())
            sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    //获取TrustManager
    private val trustManager: Array<TrustManager>
        private get() = arrayOf(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

    //获取HostnameVerifier
    val hostnameVerifier: HostnameVerifier
        get() = HostnameVerifier { s, sslSession -> true }
}