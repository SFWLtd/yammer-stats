package com.sfwltd.yammerstats;

import com.sfwltd.yammerstats.client.YammerMessageClient
import com.sfwltd.yammerstats.client.YammerUserClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.Math.min
import java.util.*

@RestController
class StatsController constructor(val yammerMessageClient: YammerMessageClient, val yammerUserClient: YammerUserClient) {

    data class LeaderboardEntry(val name: String, val likes: Int)

    @RequestMapping("/toplikes")
    fun topLikes(): List<LeaderboardEntry> {
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

        return likeMap.map { LeaderboardEntry(yammerUserClient.getUserFullName(it.key) ?: "Unknown", it.value) }
            .fold(listOf<LeaderboardEntry>()) {list, entry -> list+entry}
            .sortedByDescending { it.likes }
    }
}