package com.ict.bt_chat_app

data class msg(
    val text:String,
    val author: Boolean
){
    val isFromMe: Boolean
        get() = author
}
