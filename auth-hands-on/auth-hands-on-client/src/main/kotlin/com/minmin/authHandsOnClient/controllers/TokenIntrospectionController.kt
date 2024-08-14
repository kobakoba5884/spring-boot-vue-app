package com.minmin.authHandsOnClient.controllers

import com.minmin.authHandsOnClient.configs.ClientAppConfiguration
import com.minmin.authHandsOnClient.dtos.ClientSession
import com.minmin.authHandsOnClient.dtos.TokenResponse
import com.minmin.authHandsOnClient.services.TokenIntrospectionService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

@Controller
class TokenIntrospectionController(
    val clientSession: ClientSession,
    val clientConfig: ClientAppConfiguration,
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
        @RequestParam(name = "code", required = false) code: String?,
        @RequestParam(name = "state", required = false) state: String?,
        @RequestParam(name = "error", required = false) error: String?,
        @RequestParam(name = "error_description", required = false) errorDescription: String?,
        model: Model,
    ): String {
        error?.let {
            model.addAttribute("error", error)
            model.addAttribute("errorDescription", errorDescription)
            return "gettoken"
        }

        val headers = HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED)
        headers.setBasicAuth(clientConfig.clientId, clientConfig.clientSecret)

        var redirectUrl =
            ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/gettoken")
                .replaceQuery(null)
                .toUriString()
        val params = LinkedMultiValueMap<String, String>()
        params.add("code", code)
        params.add("grant_type", "authorization_code")
        params.add("redirect_uri", redirectUrl)

        val req =
            RequestEntity<Any>(
                params,
                headers,
                HttpMethod.POST,
                URI.create(
                    clientConfig.tokenEndpoint,
                ),
            )
        var token: TokenResponse? = null

        try {
            val res: ResponseEntity<TokenResponse> =
                RestTemplate().exchange(req, TokenResponse::class.java)
            token = res.body
        } catch (e: HttpStatusCodeException) {
            TokenResponse.withError(e.message, e.getResponseBodyAsString())
        } catch (e: ResourceAccessException) {
            TokenResponse.withError(e.message, null)
        }

        if (token?.error != null) {
            model.addAttribute("error", token.error)
            model.addAttribute("errorDescription", token.errorDescription)
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
