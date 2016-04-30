package com.sfwltd.yammerstats.client

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

interface YammerMessageClient {

    fun getMessages(olderThan: Int): JsonArray<JsonObject>

}
