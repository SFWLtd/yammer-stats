package com.sfwltd.yammerstats.controllers

import com.sfwltd.yammerstats.client.YammerExportClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse


@RestController
class UpdateController(val exportClient: YammerExportClient) {

    @RequestMapping("/update")
    fun update(response: HttpServletResponse): ByteArray {
        response.status = HttpServletResponse.SC_OK
        return exportClient.export()
    }
}