package com.sfwltd.yammerstats;

import com.beust.klaxon.JsonObject
import com.beust.klaxon.int
import com.beust.klaxon.json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class StatsController @Autowired constructor(val yammerClient: YammerClient) {

    @RequestMapping("/toplikes")
    fun topLikes(): String {
        val likeMap = HashMap<Int, Int>()
        var olderThan = Int.MAX_VALUE

        for (pages in 1..10) {
            yammerClient.getMessages(olderThan)
                .filter {(it["liked_by"] as JsonObject).int("count")!! > 0}
                .map {
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
                    olderThan = msgId!!
                }
        }

        data class LeaderboardEntry(val name:String, val likes:Int)
        val leaderboard = likeMap.map { LeaderboardEntry(yammerClient.getUserFullName(it.key), it.value) }
            .fold(listOf<LeaderboardEntry>()) {list, entry -> list+entry}
            .sortedByDescending { it.likes }

        return json {
            array(leaderboard.map { obj(Pair("name", it.name), Pair("likes", it.likes)) })
        }.toJsonString()
    }
}