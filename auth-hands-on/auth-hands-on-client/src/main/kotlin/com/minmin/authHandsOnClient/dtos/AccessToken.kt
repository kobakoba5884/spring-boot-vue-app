package com.minmin.authHandsOnClient.dtos

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class AccessToken() : JsonWebToken() {
    val scope: String? = null
}
