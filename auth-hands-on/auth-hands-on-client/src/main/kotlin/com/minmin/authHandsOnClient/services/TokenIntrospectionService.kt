package com.minmin.authHandsOnClient.services

import com.minmin.authHandsOnClient.configs.ClientAppConfiguration
import com.minmin.authHandsOnClient.configs.OauthConfiguration
import com.minmin.authHandsOnClient.dtos.ClientSession
import com.minmin.authHandsOnClient.dtos.IdToken
import com.minmin.authHandsOnClient.dtos.JsonWebToken
import com.minmin.authHandsOnClient.dtos.TokenResponse
import com.minmin.authHandsOnClient.utils.OauthUtil
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.UUID

@Service
class TokenIntrospectionService(
    val clientSession: ClientSession,
    val clientConfig: ClientAppConfiguration,
    val oauthConfig: OauthConfiguration,
) {
    private val charset = Charset.defaultCharset().toString()

    fun buildAuthorizationUrl(scope: String?): String {
        val authorizationUrl =
            UriComponentsBuilder.fromUriString(clientConfig.authorizationEndpoint)
        val params = buildAuthrizationParams(scope)

        return authorizationUrl.queryParams(params).build(true).toUriString()
    }

    fun processAuthorizationCodeGrant(
        code: String,
        state: String?,
    ): TokenResponse? {
        if (oauthConfig.state) {
            state?.takeIf { !it.equals(clientSession.state) }?.let {
                return TokenResponse.withError("state check NG.", null)
            }
        } else {
            clientSession.state = null
        }

        val token = requestToken(code)

        if (token?.error != null) {
            return token
        }

        if (oauthConfig.nonce && token?.idToken != null) {
            val idToken = token.idToken?.let { JsonWebToken.parse(it, IdToken::class) }

            if (idToken?.nonce == null || !idToken.nonce.equals(clientSession.nonce)) {
                return TokenResponse.withError("nonce check NG", null)
            } else {
                clientSession.nonce = null
            }
        }

        return token
    }

    private fun buildAuthrizationParams(scope: String?): LinkedMultiValueMap<String, String> {
        val params = LinkedMultiValueMap<String, String>()
        val recirectUri = buildRedirectUri("/gettoken", true)

        params.add("redirect_uri", recirectUri)
        params.add("response_type", "code")
        params.add("client_id", clientConfig.clientId)
        scope?.takeIf { it.isNotEmpty() }?.let {
            params.add("scope", URLEncoder.encode(it, charset))
        }

        if (oauthConfig.state) {
            val state = UUID.randomUUID().toString()
            clientSession.state = state
            params.add("state", state)
        }
        if (oauthConfig.nonce) {
            val nonce = UUID.randomUUID().toString()
            clientSession.nonce = nonce
            params.add("nonce", nonce)
        }
        if (oauthConfig.pkce) {
            val codeVerifier = OauthUtil.generateCodeVerifier()
            val codeChallenge = OauthUtil.generateCodeChallenge(codeVerifier)
            clientSession.codeVerifier = codeVerifier
            params.add("code_challenge_method", "S256")
            params.add("code_challenge", codeChallenge)
        }
        if (oauthConfig.formPost) {
            params.add("response_mode", "form_post")
        }

        return params
    }

    private fun buildRedirectUri(
        replacePath: String,
        isEncode: Boolean = false,
    ): String {
        return ServletUriComponentsBuilder.fromCurrentRequest()
            .replacePath(replacePath)
            .replaceQuery(null)
            .toUriString()
            .let { if (isEncode) URLEncoder.encode(it, charset) else it }
    }

    private fun requestToken(authorizationCode: String): TokenResponse? {
        val headers = HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED)
        headers.setBasicAuth(clientConfig.clientId, clientConfig.clientSecret)

        var redirectUrl = buildRedirectUri("/gettoken")
        val params = LinkedMultiValueMap<String, String>()
        params.add("code", authorizationCode)
        params.add("grant_type", "authorization_code")
        params.add("redirect_uri", redirectUrl)

        if (oauthConfig.pkce) {
            params.add("code_verifier", clientSession.codeVerifier)
        }

        val req =
            RequestEntity<Any>(
                params,
                headers,
                HttpMethod.POST,
                URI.create(
                    clientConfig.tokenEndpoint,
                ),
            )

        try {
            val res: ResponseEntity<TokenResponse> =
                RestTemplate().exchange(req, TokenResponse::class.java)
            return res.body
        } catch (e: HttpStatusCodeException) {
            return TokenResponse.withError(e.message, e.getResponseBodyAsString())
        } catch (e: ResourceAccessException) {
            return TokenResponse.withError(e.message, null)
        }
    }
}
