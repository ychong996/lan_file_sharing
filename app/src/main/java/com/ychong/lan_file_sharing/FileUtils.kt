package com.ychong.lan_file_sharing

import java.io.File

class FileUtils {
    companion object {
        val instance: FileUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { FileUtils() }
    }

    fun existsFile(localDir:String,fileName:String):Boolean{
        val dirFile =File(localDir)
        if (dirFile.exists()&&dirFile.isDirectory){
            val files = dirFile.listFiles()
            for (item in files){
                if (fileName == item.name){
                    return true
                }
            }
        }
        return false
    }
}