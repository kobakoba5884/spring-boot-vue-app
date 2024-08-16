package com.minmin.authHandsOnClient.configs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "oauth.config")
class OauthConfiguration {
    var state: Boolean = false
    var nonce: Boolean = false
    var pkce: Boolean = false
    var formPost: Boolean = false
}
