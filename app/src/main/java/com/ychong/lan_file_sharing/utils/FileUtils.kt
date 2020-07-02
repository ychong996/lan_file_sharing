package com.ychong.lan_file_sharing.utils

import java.io.File
import java.lang.Exception

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
    fun deleteFile(localDir: String,fileName: String):Boolean{
        try {
            val file = File(localDir+fileName)
            if (file.exists()){
                return file.delete()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return false

    }
}