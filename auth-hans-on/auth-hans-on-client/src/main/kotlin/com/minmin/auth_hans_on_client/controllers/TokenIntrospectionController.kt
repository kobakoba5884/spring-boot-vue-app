package com.minmin.auth_hans_on_client.controllers

import java.net.URI
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.RestTemplate

@Controller
class TokenIntrospectionController {

    @RequestMapping("/")
    fun index(): String {
        return "index"
    }

    @RequestMapping("/call-health")
    fun callHealth(model: Model): String? {
        val headers = HttpHeaders()
        val req =
                RequestEntity<String>(
                        headers,
                        HttpMethod.GET,
                        URI.create("http://localhost:8888/token-introspection/health")
                )

        val res = RestTemplate().exchange(req, String::class.java)
        val response = res.getBody()

        model.addAttribute("apiResponse", response)

        return "forward:/"
    }
}
