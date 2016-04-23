package com.sfwltd.yammerstats

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.sfwltd.yammerstats.StatsConfiguration.YammerConfig
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
    fun countsLikes() {
        val wireMockServer = WireMockServer(8089)
        wireMockServer.start()
        configureFor("localhost", 8089)
        stubFor(get(urlPathMatching("/api/v1/messages.json")).withQueryParam("older_than", containing("2147483647")).atPriority(1).willReturn(aResponse().withBody("""
        {
                "messages": [
                {
                    "id": 8,
                    "sender_id": 1,
                },
                    "liked_by": {
                        "count": 1,
                        "names": [
                            {
                                "full_name": "Ann Onymous",
                                "permalink": "anonymous",
                                "user_id": 99,
                                "network_id": 2222
                            }
                        ]
                    },
                }]
        }
        """)))

        stubFor(get(urlPathEqualTo("/api/v1/users/1.json")).willReturn(aResponse().withBody("""
        {
            "full_name": "Adam Londero"
        }
        """)))
        val mvc = MockMvcBuilders.standaloneSetup(StatsController(FuelYammerClient(YammerConfig(host = "http://localhost:8089", accessToken = "anything")))).build()
        mvc.perform(MockMvcRequestBuilders.get("/toplikes")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().json("""[{"name":"Adam Londero", "likes": 1}]"""))
        wireMockServer.stop()
    }
}
