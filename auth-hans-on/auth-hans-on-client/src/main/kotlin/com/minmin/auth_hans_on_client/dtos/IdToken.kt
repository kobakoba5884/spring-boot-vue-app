package com.minmin.auth_hans_on_client.dtos

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class IdToken(val nonce: String) : JsonWebToken() {}
