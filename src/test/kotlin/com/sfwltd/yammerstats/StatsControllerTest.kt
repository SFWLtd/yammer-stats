package com.sfwltd.yammerstats

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
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
        stubFor(get(urlPathMatching("/api/v1/messages.json")).willReturn(aResponse().withBody("""
        {
                "threaded_extended": {},
                "messages": [
                {
                    "id": 8,
                    "sender_id": 1,
                    "replied_to_id": 9,
                    "created_at": "2016/04/21 07:41:03 +0000",
                    "network_id": 2222,
                    "message_type": "update",
                    "sender_type": "user",
                    "url": "https://www.yammer.com/api/v1/messages/692534234",
                    "web_url": "https://www.yammer.com/sfwltd.co.uk/messages/692534234",
                    "group_id": 10,
                    "body": {
                    "parsed": "Text",
                    "plain": "Text",
                    "rich": "Text"
                },
                    "thread_id": 11,
                    "client_type": "Web",
                    "client_url": "https://www.yammer.com/",
                    "system_message": false,
                    "direct_message": false,
                    "chat_client_sequence": null,
                    "language": "en",
                    "notified_user_ids": [],
                    "privacy": "public",
                    "attachments": [],
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
                    "content_excerpt": "Text",
                    "group_created_id": 10
                }]
        }
        """)))

        stubFor(get(urlPathEqualTo("/api/v1/users/1.json")).willReturn(aResponse().withBody("""
        {
            "full_name": "Adam Londero"
        }
        """)))
        val mvc = MockMvcBuilders.standaloneSetup(StatsController(StatsConfiguration.YammerConfig(host="http://localhost:8089", accessToken = "anything"))).build()
        mvc.perform(MockMvcRequestBuilders.get("/toplikes")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().json("""{"Adam Londero": 1}"""))
        wireMockServer.stop()
    }
}
