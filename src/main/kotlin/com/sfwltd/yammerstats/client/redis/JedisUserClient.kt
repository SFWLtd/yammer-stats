package com.sfwltd.yammerstats.client.redis

import com.sfwltd.yammerstats.client.YammerUserClient
import redis.clients.jedis.JedisPool

/**
 * Attempts to retrieve a user name from a Redis cache, falling back to a delegate YammerUserClient if not found
 */
class JedisUserClient(val jedisPool: JedisPool, val delegate: YammerUserClient) : YammerUserClient {
    override fun getUserFullName(id: Int) =
            jedisPool.resource.use { resource ->
                resource.get(id.toString()) ?: delegate.getUserFullName(id)?.apply { resource.set(id.toString(), this) }
            }
}
