package com.minmin.authHandsOnClient.controllers

import com.minmin.authHandsOnClient.dtos.ClientSession
import com.minmin.authHandsOnClient.dtos.TokenResponse
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
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.Charset

@Controller
class TokenIntrospectionController(val clientSession: ClientSession) {
    @RequestMapping("/")
    fun index(): String {
        return "index"
    }

    @PostMapping("/auth")
    fun auth(
        @RequestParam(name = "scope", required = false) scope: String?,
    ): String {
        clientSession.scope = scope

        val authorizationUrl =
            UriComponentsBuilder.fromUriString(
                "http://localhost:8080/realms/auth-hands-on/protocol/openid-connect/auth",
            )
        val params = LinkedMultiValueMap<String, String>()
        val charset = Charset.defaultCharset().toString()
        var redirectUrl =
            ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/gettoken")
                .replaceQuery(null)
                .toUriString()
        redirectUrl = URLEncoder.encode(redirectUrl, charset)
        params.add("redirect_uri", redirectUrl)
        params.add("response_type", "code")
        params.add("client_id", "auth-hands-on-client")

        if (scope != null && !scope.isEmpty()) {
            params.add("scope", URLEncoder.encode(scope, charset))
        }

        val authUrl = authorizationUrl.queryParams(params).build(true).toUriString()

        return String.format("redirect:%s", authUrl)
    }

    @GetMapping("/gettoken")
    fun getToken(
        @RequestParam(name = "code", required = false) code: String,
        @RequestParam(name = "error", required = false) error: String?,
        @RequestParam(name = "error_description", required = false) errorDescription: String?,
        model: Model,
    ): String {
        if (error != null) {
            model.addAttribute("error", error)
            model.addAttribute("errorDescription", errorDescription)
            return "gettoken"
        }

        val headers = HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED)
        // TODO
        headers.setBasicAuth("auth-hands-on-client", "SQRKMhG1pVLmKibdI5JWznD6w9GPXJWo")

        // val charset = Charset.defaultCharset().toString()
        var redirectUrl =
            ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/gettoken")
                .replaceQuery(null)
                .toUriString()
        // redirectUrl = URLEncoder.encode(redirectUrl, charset)
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
                    "http://keycloak:8080/realms/auth-hands-on/protocol/openid-connect/token",
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
                URI.create("http://localhost:8888/token-introspection/health"),
            )

        val res = RestTemplate().exchange(req, String::class.java)
        val response = res.getBody()

        model.addAttribute("apiResponse", response)

        return "forward:/"
    }
}
