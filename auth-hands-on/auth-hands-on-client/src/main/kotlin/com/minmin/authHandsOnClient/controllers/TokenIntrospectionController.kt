package com.minmin.authHandsOnClient.controllers

import com.minmin.authHandsOnClient.configs.ClientAppConfiguration
import com.minmin.authHandsOnClient.configs.OauthConfiguration
import com.minmin.authHandsOnClient.dtos.ClientSession
import com.minmin.authHandsOnClient.services.TokenIntrospectionService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.RestTemplate
import java.net.URI

@Controller
class TokenIntrospectionController(
    val clientSession: ClientSession,
    val clientConfig: ClientAppConfiguration,
    val oauthConfig: OauthConfiguration,
    val tokenIntrospectionService: TokenIntrospectionService,
) {
    @RequestMapping("/")
    fun index(): String {
        return "index"
    }

    @PostMapping("/auth")
    fun auth(
        @RequestParam(name = "scope", required = false) scope: String?,
    ): String {
        clientSession.scope = scope
        val authUrl = tokenIntrospectionService.buildAuthorizationUrl(scope)

        return String.format("redirect:%s", authUrl)
    }

    @GetMapping("/gettoken")
    fun getToken(
        @RequestParam(name = "code", required = false) code: String,
        @RequestParam(name = "state", required = false) state: String?,
        @RequestParam(name = "error", required = false) error: String?,
        @RequestParam(name = "error_description", required = false) errorDescription: String?,
        model: Model,
    ): String {
        if (oauthConfig.formPost) {
            return "gettoken"
        }

        error?.let {
            model.addAttribute("error", error)
            model.addAttribute("errorDescription", errorDescription)
            return "gettoken"
        }

        val token = tokenIntrospectionService.processAuthorizationCodeGrant(code, state)

        if (token?.error != null) {
            model.addAttribute("error", token.error)
            model.addAttribute("errorDescription", token.errorDescription)
            return "gettoken"
        }

        clientSession.setTokensFromTokenResponse(token)
        return "gettoken"
    }

    @RequestMapping("/call-health")
    fun callHealth(model: Model): String? {
        val headers = HttpHeaders()
        val req =
            RequestEntity<String>(
                headers,
                HttpMethod.GET,
                URI.create(clientConfig.apiserverUrl + "/health"),
            )

        val res = RestTemplate().exchange(req, String::class.java)
        val response = res.getBody()

        model.addAttribute("apiResponse", response)

        return "forward:/"
    }
}
