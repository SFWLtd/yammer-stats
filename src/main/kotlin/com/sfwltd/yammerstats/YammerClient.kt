package com.sfwltd.yammerstats

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

interface YammerClient {

    fun getMessages(olderThan: Int): JsonArray<JsonObject>

    fun getUserFullName(id: Int): String
}
