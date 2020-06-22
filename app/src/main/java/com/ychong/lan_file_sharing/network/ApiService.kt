package com.ychong.lan_file_sharing.network

import io.reactivex.Observable
import okhttp3.ResponseBody

interface ApiService {
    fun getInfo():Observable<ResponseBody>
}