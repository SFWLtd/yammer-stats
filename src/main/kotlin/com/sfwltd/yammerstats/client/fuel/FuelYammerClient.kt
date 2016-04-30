package com.sfwltd.yammerstats.client.fuel

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.string
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.sfwltd.yammerstats.StatsConfiguration
import com.sfwltd.yammerstats.client.YammerMessageClient
import com.sfwltd.yammerstats.client.YammerUserClient
import org.springframework.beans.factory.annotation.Autowired
import java.io.ByteArrayInputStream

class FuelYammerClient @Autowired constructor(val yammerConfig: StatsConfiguration.YammerConfig): YammerMessageClient, YammerUserClient {

    val parser: Parser;

    init {
        FuelManager.instance.basePath = yammerConfig.host
        parser = Parser()
    }

    override fun getMessages(olderThan: Int): JsonArray<JsonObject> {
        val (request, response, result) = "/api/v1/messages.json".httpGet(listOf("older_than" to olderThan)).run {
            httpHeaders.put("Authorization", "Bearer ${yammerConfig.accessToken}")
            response()
        }
        when (result) {
            is Result.Success -> {
                return (parser.parse(ByteArrayInputStream(response.data)) as JsonObject)["messages"] as JsonArray<JsonObject>
            }
            is Result.Failure -> {
                return JsonArray()
            }
        }
    }

    override fun getUserFullName(id: Int): String? {
        val (request, response, result) = "/api/v1/users/$id.json".httpGet().run {
            httpHeaders.put("Authorization", "Bearer ${yammerConfig.accessToken}")
            response()
        }
        when (result) {
            is Result.Success -> {
                return (parser.parse(ByteArrayInputStream(response.data)) as JsonObject).string("full_name")!!
            }
            is Result.Failure -> {
                return null
            }
        }
    }

}