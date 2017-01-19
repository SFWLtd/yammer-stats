package com.sfwltd.yammerstats

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
open class StatsApplication {

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(StatsApplication::class.java, *args)
        }
    }
}

