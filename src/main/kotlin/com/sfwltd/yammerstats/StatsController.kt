package com.sfwltd.yammerstats;

import com.beust.klaxon.*
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayInputStream
import java.util.*

@RestController
class StatsController @Autowired constructor(val yammerConfig: StatsConfiguration.YammerConfig) {

    @RequestMapping("/toplikes")
    fun topLikes(): String {
        FuelManager.instance.basePath = yammerConfig.host
        val likeMap = HashMap<Int, Int>()
        val parser = Parser()
        var olderThan = Int.MAX_VALUE

        for (pages in 1..10) {
            val yammerRequest = "/api/v1/messages.json".httpGet(listOf("older_than" to olderThan))
            yammerRequest.httpHeaders.put("Authorization", "Bearer ${yammerConfig.accessToken}")
            val (request, response, result) = yammerRequest.response()
            when (result) {
                is Result.Success -> {
                    val responseData = parser.parse(ByteArrayInputStream(response.data)) as JsonObject
                    val messages = responseData["messages"] as JsonArray<JsonObject>
                    messages.filter {
                        (it["liked_by"] as JsonObject).int("count")!! > 0
                    }.map {
                        Triple(it.int("sender_id"),
                                (it["liked_by"] as JsonObject).int("count"),
                                it.int("id"))
                    }.forEach {
                        val (msgAuthorId, msgLikes, msgId) = it
                        if (!likeMap.containsKey(msgAuthorId)) {
                            likeMap.put(msgAuthorId!!, msgLikes!!)
                        } else {
                            likeMap.replace(msgAuthorId!!, likeMap[msgAuthorId]!!.plus(msgLikes!!))
                        }
                        olderThan = msgId!!;
                    }
                }
            }
        }

        val leaderboard = HashMap<String, Int>()
        likeMap.forEach {
            val (authorId, likes) = it;
            val userRequest = "/api/v1/users/$authorId.json".httpGet()
            userRequest.httpHeaders.put("Authorization", "Bearer ${yammerConfig.accessToken}")
            val (request, response, result) = userRequest.response()
            when (result) {
                is Result.Success -> {
                    val responseData = parser.parse(ByteArrayInputStream(response.data)) as JsonObject
                    leaderboard.put(responseData.string("full_name")!!, likes)
                }
            }
        }

        return JsonObject(leaderboard).toJsonString()
    }
}