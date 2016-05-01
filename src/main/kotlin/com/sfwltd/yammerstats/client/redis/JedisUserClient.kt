package com.sfwltd.yammerstats.client.redis

import com.sfwltd.yammerstats.client.YammerUserClient
import redis.clients.jedis.JedisPool

/**
 * Attempts to retrieve a user name from a Redis cache, falling back to a delegate YammerUserClient if not found
 */
class JedisUserClient(val jedisPool: JedisPool, val delegate: YammerUserClient) : YammerUserClient {
    override fun getUserFullName(id: Int): String? =
            lambda@ jedisPool.resource.use {
                it.get(id.toString()) ?: delegate.getUserFullName(id)?.apply { lambda@it.set(id.toString(), this) }
            }
}
