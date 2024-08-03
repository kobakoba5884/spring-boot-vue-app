package com.minmin.auth_hans_on_client.dtos

import com.minmin.auth_hans_on_client.dtos.TokenResponse
import com.minmin.auth_hans_on_client.dtos.AccessToken
import com.minmin.auth_hans_on_client.dtos.IdToken
import com.minmin.auth_hans_on_client.dtos.JsonWebToken
import com.minmin.auth_hans_on_client.dtos.RefreshToken
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
data class ClientSession(
    var optional: String?,
    var idToken: IdToken?,
    var accessToken: AccessToken?,
    var refreshToken: RefreshToken?,
    var state: String?,
    var nonce: String?,
    var scope: String?,
    var codeVerifier: String?
) {

    fun setTokensFromTokenResponse(response: TokenResponse?) {
        this.accessToken = JsonWebToken.parse(response?.accessToken ?: "", AccessToken::class)
        this.refreshToken = JsonWebToken.parse(response?.refreshToken ?: "", RefreshToken::class)
    }
}