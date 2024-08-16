package com.minmin.authHandsOnClient.utils

import java.security.MessageDigest
import java.security.SecureRandom

object OauthUtil {
    fun generateCodeVerifier(): String {
        val secureRandom = SecureRandom()
        val octets = ByteArray(32)
        secureRandom.nextBytes(octets)
        return Base64UrlUtil.encode(octets)
    }

    fun generateCodeChallenge(codeVerifier: String): String {
        val md = MessageDigest.getInstance("SHA-256")

        md.update(codeVerifier.toByteArray(Charsets.ISO_8859_1))

        val digestBytes = md.digest()
        return Base64UrlUtil.encode(digestBytes)
    }
}
