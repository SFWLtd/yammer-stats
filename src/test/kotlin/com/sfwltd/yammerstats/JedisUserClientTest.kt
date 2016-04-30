package com.sfwltd.yammerstats

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.sfwltd.yammerstats.client.YammerUserClient
import com.sfwltd.yammerstats.client.redis.JedisUserClient
import org.junit.Assert.fail
import org.junit.Test
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisShardInfo

class JedisUserClientTest {

    val jedisClient: Jedis

    init {
        val shardInfo = JedisShardInfo("sfwyammerstats.redis.cache.windows.net", 6379)
        shardInfo.password = "U5v0fBNINzqldhmCIFF//8M0d1xUj1NNjwBpO/4f1nU="
        jedisClient = Jedis(shardInfo)
    }

    @Test
    fun retrievesFromCache() {
        jedisClient.del("1")
        val client = JedisUserClient(jedisClient, object : YammerUserClient {
            override fun getUserFullName(id: Int): String? {
                fail("Should not be called")
                return ""
            }
        })
        jedisClient.set("1", "Testy McTestface")
        assertThat(client.getUserFullName(1)!!, equalTo("Testy McTestface"))
    }

    @Test
    fun delegatesIfNotContainedInCache() {
        jedisClient.del("2")
        val client = JedisUserClient(jedisClient, object : YammerUserClient {
            override fun getUserFullName(id: Int): String? {
                return "From delegate"
            }
        })
        assertThat(client.getUserFullName(2)!!, equalTo("From delegate"))
    }

    @Test
    fun savesDelegatedResultInCache() {
        jedisClient.del("3")
        var jedisCalled = 0
        val client = JedisUserClient(jedisClient, object : YammerUserClient {
            override fun getUserFullName(id: Int): String? {
                jedisCalled++
                return "From delegate"
            }
        })

        for (i in 1..5) {
            client.getUserFullName(3)
        }

        assertThat(jedisCalled, equalTo(1))
    }

    @Test
    fun doesNotSaveWhenDelegateDidntFindName() {
        jedisClient.del("4")
        var jedisCalled = 0
        val client = JedisUserClient(jedisClient, object : YammerUserClient {
            override fun getUserFullName(id: Int): String? {
                jedisCalled++
                return null
            }
        })

        for (i in 1..5) {
            client.getUserFullName(4)
        }

        assertThat(jedisCalled, equalTo(5))
    }
}