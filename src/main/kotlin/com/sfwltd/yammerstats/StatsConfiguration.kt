package com.sfwltd.yammerstats

import com.sfwltd.yammerstats.client.fuel.FuelYammerClient
import com.sfwltd.yammerstats.client.redis.JedisUserClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisShardInfo

@Configuration
open class StatsConfiguration {

    data class YammerConfig(val host:String = "https://yammer.com", val accessToken: String)

    @Autowired lateinit var env:Environment;

    @Bean open fun yammerUserClient() = JedisUserClient(jedis(), fuelYammerClient())
    @Bean open fun yammerMessageClient() = fuelYammerClient()
    @Bean open fun fuelYammerClient() = FuelYammerClient(yammerConfig())
    @Bean open fun yammerConfig() = YammerConfig(accessToken = env.getRequiredProperty("yammer.accesstoken"))
    @Bean open fun jedis() = Jedis(JedisShardInfo(env.getRequiredProperty("redis.host"), env.getProperty("redis.port", Int::class.java, 6379))
            .apply {password = env.getRequiredProperty("redis.password")})

}
