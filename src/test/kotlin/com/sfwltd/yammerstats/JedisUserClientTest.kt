package com.sfwltd.yammerstats

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.sfwltd.yammerstats.client.YammerUserClient
import com.sfwltd.yammerstats.client.redis.JedisUserClient
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import redis.clients.jedis.JedisPool

@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = arrayOf(StatsConfiguration::class))
@TestPropertySource("file:application.properties")
class JedisUserClientTest {

    @Autowired lateinit var jedisPool: JedisPool

    @Test
    fun retrievesFromCache() {
        jedisPool.resource.use {
            it.del("1")
            it.set("1", "Testy McTestface")
        }

        val client = JedisUserClient(jedisPool, object : YammerUserClient {
            override fun getUserFullName(id: Int): String? {
                fail("Should not be called")
                return ""
            }
        })
        assertThat(client.getUserFullName(1)!!, equalTo("Testy McTestface"))
    }

    @Test
    fun delegatesIfNotContainedInCache() {
        jedisPool.resource.use {
            it.del("2")
        }
        val client = JedisUserClient(jedisPool, object : YammerUserClient {
            override fun getUserFullName(id: Int): String? {
                return "From delegate"
            }
        })
        assertThat(client.getUserFullName(2)!!, equalTo("From delegate"))
    }

    @Test
    fun savesDelegatedResultInCache() {
        jedisPool.resource.use {
            it.del("3")
        }
        var jedisCalled = 0
        val client = JedisUserClient(jedisPool, object : YammerUserClient {
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
        jedisPool.resource.use {
            it.del("4")
        }
        var jedisCalled = 0
        val client = JedisUserClient(jedisPool, object : YammerUserClient {
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