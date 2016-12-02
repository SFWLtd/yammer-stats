package com.sfwltd.yammerstats

import com.sfwltd.yammerstats.client.fuel.FuelYammerClient
import com.sfwltd.yammerstats.client.redis.JedisUserClient
import com.sfwltd.yammerstats.controllers.StatsController
import com.sfwltd.yammerstats.controllers.UpdateController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

@Configuration
open class StatsConfiguration {

    data class YammerConfig(val host:String = "https://yammer.com", val accessToken: String)

    @Autowired lateinit var env:Environment

    @Bean open fun statsController() = StatsController(yammerMessageClient(), fuelYammerClient())
    @Bean open fun updateController() = UpdateController()
    @Bean open fun yammerUserClient() = JedisUserClient(jedisPool(), fuelYammerClient())
    @Bean open fun yammerMessageClient() = fuelYammerClient()
    @Bean open fun fuelYammerClient() = FuelYammerClient(yammerConfig())
    @Bean open fun yammerConfig() = YammerConfig(accessToken = env.getRequiredProperty("yammer.accesstoken"))
    @Bean open fun jedisPool() = JedisPool(JedisPoolConfig(), env.getRequiredProperty("redis.host"), env.getProperty("redis.port", Int::class.java, 6379), env.getProperty("redis.timeout", Int::class.java, 5000), env.getRequiredProperty("redis.password"))

}
