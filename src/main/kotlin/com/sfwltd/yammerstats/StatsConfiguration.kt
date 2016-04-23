package com.sfwltd.yammerstats

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
open class StatsConfiguration {

    data class YammerConfig(val host:String = "https://yammer.com", val accessToken: String)

    @Bean
    open fun yammerClient():YammerClient {
        return FuelYammerClient(yammerConfig())
    }

    @Bean
    open fun yammerConfig():YammerConfig {
        val accessTokenFile = File("accesstoken")

        if (!accessTokenFile.exists()) {
            throw RuntimeException("Access Token file does not exist. Place your accesstoken in a file called 'accesstoken' alongside the JAR")
        }

        return YammerConfig(accessToken = accessTokenFile.readText())
    }
}
