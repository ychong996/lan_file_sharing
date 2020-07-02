package com.ychong.lan_file_sharing.data

import org.apache.commons.net.ftp.FTPFile

data class FileBean(
    var id:Int,
    var name:String = "",
    var localPath:String = "",
    var remotePath:String = "",
    var fileType:String = "",
    var isDownload:Boolean = false
    )