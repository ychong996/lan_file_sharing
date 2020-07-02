package com.ychong.lan_file_sharing.data

import org.apache.commons.net.ftp.FTPFile

data class FileBean(val ftpFile:FTPFile, var isDownload:Boolean = false,var localPath:String = "")