package com.sfwltd.yammerstats;

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StatsController {
    @RequestMapping("/")
    fun index(): String {
        return "Hello Adam!"
    }
}