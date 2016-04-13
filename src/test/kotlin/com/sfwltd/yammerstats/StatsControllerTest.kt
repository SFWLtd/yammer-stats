package com.sfwltd.yammerstats

import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.http.MediaType
import org.springframework.mock.web.MockServletContext
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@RunWith(SpringJUnit4ClassRunner::class)
@WebAppConfiguration
@SpringApplicationConfiguration(MockServletContext::class)
class HelloControllerTest {

    @Test
    @Throws(Exception::class)
    fun getHello() {
        val mvc = MockMvcBuilders.standaloneSetup(StatsController()).build()
        mvc.perform(MockMvcRequestBuilders.get("/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Hello Adam!")))
    }
}
