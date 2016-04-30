package com.sfwltd.yammerstats.client.redis

import com.sfwltd.yammerstats.client.YammerUserClient
import redis.clients.jedis.Jedis

/**
 * Attempts to retrieve a user name from a Redis cache, falling back to a delegate YammerUserClient if not found
 */
class JedisUserClient(val jedisClient: Jedis, val delegate: YammerUserClient) : YammerUserClient {
    override fun getUserFullName(id: Int): String? =
            jedisClient.get(id.toString()) ?: delegate.getUserFullName(id)?.apply { jedisClient.set(id.toString(), this) }
}
