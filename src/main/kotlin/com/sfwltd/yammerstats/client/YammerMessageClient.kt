package com.sfwltd.yammerstats.client

interface YammerMessageClient {

    data class YammerMessage(val id:Int, val likes:Int, val senderId:Int)

    fun getMessages(olderThan: Int): List<YammerMessage>

}
