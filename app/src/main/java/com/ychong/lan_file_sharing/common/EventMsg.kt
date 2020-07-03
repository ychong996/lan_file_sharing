package com.ychong.lan_file_sharing.common

data class EventMsg(var type:String, var msg:String = "", var obj: Any? =null)