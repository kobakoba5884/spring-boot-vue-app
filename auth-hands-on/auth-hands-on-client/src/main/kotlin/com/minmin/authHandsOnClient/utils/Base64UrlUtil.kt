package com.minmin.authHandsOnClient.utils

import java.util.Base64

object Base64UrlUtil {
    fun encode(octets: ByteArray): String {
        var encoded = Base64.getEncoder().encodeToString(octets)
        encoded = encoded.split("=")[0]
        encoded = encoded.replace('+', '-')
        encoded = encoded.replace('/', '_')
        return encoded
    }

    fun decode(encodedContent: String): ByteArray {
        var encodedContentVar = encodedContent.replace('-', '+').replace('_', '/')
        when (encodedContentVar.length % 4) {
            2 -> encodedContentVar += "=="
            3 -> encodedContentVar += "="
        }
        return Base64.getDecoder().decode(encodedContentVar)
    }
}
