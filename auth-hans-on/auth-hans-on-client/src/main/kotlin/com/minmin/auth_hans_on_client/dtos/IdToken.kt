package com.minmin.auth_hans_on_client.dtos

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class IdToken(val scope: String) : JsonWebToken() {}
