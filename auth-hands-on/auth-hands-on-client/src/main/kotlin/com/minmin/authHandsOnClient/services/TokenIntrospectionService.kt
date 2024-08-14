package com.minmin.authHandsOnClient.services

import com.minmin.authHandsOnClient.configs.ClientAppConfiguration
import com.minmin.authHandsOnClient.configs.OauthConfiguration
import com.minmin.authHandsOnClient.dtos.ClientSession
import com.minmin.authHandsOnClient.utils.OauthUtil
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.util.UriComponentsBuilder
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

    private fun buildAuthrizationParams(scope: String?): LinkedMultiValueMap<String, String> {
        val params = LinkedMultiValueMap<String, String>()
        val recirectUri = buildRedirectUri("/gettoken")

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

    private fun buildRedirectUri(replacePath: String): String {
        return ServletUriComponentsBuilder.fromCurrentRequest()
            .replacePath(replacePath)
            .replaceQuery(null)
            .toUriString()
            .let { URLEncoder.encode(it, charset) }
    }
}
