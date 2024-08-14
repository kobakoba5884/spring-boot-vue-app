package com.minmin.authHandsOnClient.configs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "clientapp.config")
class ClientAppConfiguration {
    lateinit var apiserverUrl: String
    lateinit var authorizationEndpoint: String
    lateinit var tokenEndpoint: String
    lateinit var revokeEndpoint: String
    lateinit var clientId: String
    lateinit var clientSecret: String
    lateinit var scope: String
}
