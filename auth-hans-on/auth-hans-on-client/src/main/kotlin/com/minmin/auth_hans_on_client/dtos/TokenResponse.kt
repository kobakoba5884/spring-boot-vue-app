package com.minmin.auth_hans_on_client.dtos

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TokenResponse(
        var accessToken: String? = null,
        var expiresIn: Int = 0,
        var refreshExpiresIn: Int = 0,
        var refreshToken: String? = null,
        var idToken: String? = null,
        var tokenType: String? = null,
        var notBeforePolicy: Int = 0,
        var sessionState: String? = null,
        var scope: String? = null,
        var error: String? = null,
        var errorDescription: String? = null
) {
    companion object {
        fun withError(error: String?, description: String?): TokenResponse {
            return TokenResponse(error = error, errorDescription = description)
        }
    }
}
