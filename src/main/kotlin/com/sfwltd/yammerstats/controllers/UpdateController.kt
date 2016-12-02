package com.sfwltd.yammerstats.controllers

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse


@RestController
class UpdateController {

    @RequestMapping(method = arrayOf(RequestMethod.POST), path = arrayOf("/update"))
    fun update(response: HttpServletResponse) {
        response.status = HttpServletResponse.SC_OK
    }
}