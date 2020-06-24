package com.ychong.lan_file_sharing

import org.apache.commons.net.ftp.FTPFile

data class FileBean(val ftpFile:FTPFile, var isDownload:Boolean = false,var localPath:String = "")