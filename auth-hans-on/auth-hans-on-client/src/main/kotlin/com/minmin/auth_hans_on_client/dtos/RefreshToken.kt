package com.minmin.auth_hans_on_client.dtos

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.minmin.auth_hans_on_client.dtos.JsonWebToken

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class RefreshToken : JsonWebToken()
