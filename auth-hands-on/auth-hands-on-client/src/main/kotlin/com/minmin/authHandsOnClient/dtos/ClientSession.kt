package com.minmin.authHandsOnClient.dtos

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
    var codeVerifier: String?,
) {
    fun setTokensFromTokenResponse(response: TokenResponse?) {
        this.accessToken = JsonWebToken.parse(response?.accessToken ?: "", AccessToken::class)
        this.refreshToken = JsonWebToken.parse(response?.refreshToken ?: "", RefreshToken::class)
    }
}
