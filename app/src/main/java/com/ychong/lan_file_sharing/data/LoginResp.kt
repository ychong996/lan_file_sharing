package com.ychong.lan_file_sharing.data

data class LoginResp(
    var id: Int,
    var account: String,
    var password: String,
    var ftpAccount: String,
    var ftpPassword: String,
    var ftpIp: String,
    var ftpPort: Int
)