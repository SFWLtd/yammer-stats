package com.sfwltd.yammerstats.client.fuel

import com.beust.klaxon.*
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.sfwltd.yammerstats.StatsConfiguration
import com.sfwltd.yammerstats.client.YammerExportClient
import com.sfwltd.yammerstats.client.YammerMessageClient
import com.sfwltd.yammerstats.client.YammerUserClient
import org.springframework.beans.factory.annotation.Autowired
import java.io.ByteArrayInputStream
import java.time.LocalDateTime

class FuelYammerClient constructor(val yammerConfig: StatsConfiguration.YammerConfig): YammerMessageClient, YammerUserClient, YammerExportClient {

    val parser: Parser

    init {
        FuelManager.instance.basePath = yammerConfig.host
        parser = Parser()
    }

    override fun getMessages(olderThan: Int): List<YammerMessageClient.YammerMessage> =
        "/api/v1/messages.json".httpGet(listOf("older_than" to olderThan)).callWithAuth()?.
                array<JsonObject>("messages")?.
                map {
                    YammerMessageClient.YammerMessage(it.int("id")!!,
                            (it["liked_by"] as JsonObject).int("count")!!,
                            it.int("sender_id")!!)
                }
                ?: emptyList()

    override fun getUserFullName(id: Int): String? = "/api/v1/users/$id.json".httpGet().callWithAuth()?.string("full_name")

    override fun export(from: LocalDateTime, to: LocalDateTime): ByteArray {
        val (_, response, result) = FuelManager().request(Method.POST, "${yammerConfig.exportHost}/api/v1/export", null).run {
            httpHeaders.put("Authorization", "Bearer ${yammerConfig.accessToken}")
            httpHeaders.put("Accept", "application/zip")
            response()
        }

        when (result) {
            is Result.Success -> return parser.parse(ByteArrayInputStream(response.data)) as ByteArray
            is Result.Failure -> return ByteArray(0)
        }
    }

    private fun Request.callWithAuth(): JsonObject? {
        val (_, response, result) = this.run {
            httpHeaders.put("Authorization", "Bearer ${yammerConfig.accessToken}")
            response()
        }

        when (result) {
            is Result.Success -> return parser.parse(ByteArrayInputStream(response.data)) as JsonObject
            is Result.Failure -> return null
        }
    }
}

