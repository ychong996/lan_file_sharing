package com.ychong.lan_file_sharing.data

data class BaseResp<T>(var success:Boolean,var errorMsg:String,var resultBody:T) {
    override fun toString(): String {
        return "BaseResp(success=$success, errorMsg='$errorMsg', resultBody=$resultBody)"
    }
}