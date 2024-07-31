package com.minmin.auth_hans_on_client.controllers

import java.net.URI
import java.net.URLEncoder
import java.nio.charset.Charset
import org.springframework.ui.Model
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Controller
import org.springframework.web.client.RestTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.util.LinkedMultiValueMap

@Controller
class TokenIntrospectionController {

    @RequestMapping("/")
    fun index(): String {
        return "index"
    }

    @PostMapping( "/auth")
    fun auth() : String{
        val authorizationUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/auth/realms/auth-hands-on-api/protocol/openid-connect/auth")
        val params = LinkedMultiValueMap<String, String>()
        val charset = Charset.defaultCharset().toString()
        var redirectUrl = ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/gettoken").replaceQuery(null).toUriString()
        redirectUrl = URLEncoder.encode(redirectUrl, charset)
        params.add("redirect_uri", redirectUrl)
        params.add("response_type", "code")
        params.add("client_id", "auth-hands-on-client")
        val authUrl = authorizationUrl.queryParams(params).build(true).toUriString()

        return String.format("redirect:%s", authUrl)
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
