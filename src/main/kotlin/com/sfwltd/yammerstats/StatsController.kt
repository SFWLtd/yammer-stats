package com.sfwltd.yammerstats;

import com.beust.klaxon.json
import com.sfwltd.yammerstats.client.YammerMessageClient
import com.sfwltd.yammerstats.client.YammerUserClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.Math.min
import java.util.*

@RestController
class StatsController @Autowired constructor(val yammerMessageClient: YammerMessageClient, val yammerUserClient: YammerUserClient) {

    @RequestMapping("/toplikes")
    fun topLikes(): String {
        val likeMap = HashMap<Int, Int>()
        var olderThan = Int.MAX_VALUE

        for (pages in 1..10) {
            yammerMessageClient.getMessages(olderThan)
                .filter {it.likes > 0}
                .forEach {
                    likeMap[it.senderId] = likeMap.getOrDefault(it.senderId, 0) + it.likes
                    olderThan = min(it.id, olderThan)
                }
        }

        data class LeaderboardEntry(val name:String, val likes:Int)
        val leaderboard = likeMap.map { LeaderboardEntry(yammerUserClient.getUserFullName(it.key) ?: "Unknown", it.value) }
            .fold(listOf<LeaderboardEntry>()) {list, entry -> list+entry}
            .sortedByDescending { it.likes }

        return json {
            array(leaderboard.map { obj("name" to it.name, "likes" to it.likes) })
        }.toJsonString()
    }
}