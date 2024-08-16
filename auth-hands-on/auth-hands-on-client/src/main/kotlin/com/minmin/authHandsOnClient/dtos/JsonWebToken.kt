package com.minmin.authHandsOnClient.dtos

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.minmin.authHandsOnClient.utils.Base64UrlUtil
import com.minmin.authHandsOnClient.utils.JsonUtil
import java.util.Date
import kotlin.reflect.KClass

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
open class JsonWebToken(
    var iss: String? = null,
    var sub: String? = null,
    var aud: Array<String>? = null,
    var exp: Long = 0,
    var nbf: Long = 0,
    var iat: Long = 0,
    var jti: String? = null,
    @JsonIgnore var payload: ByteArray? = null,
    @JsonIgnore var payloadString: String? = null,
    @JsonIgnore var signature: ByteArray? = null,
    @JsonIgnore var header: Header? = null,
    @JsonIgnore var tokenString: String? = null,
) {
    companion object {
        fun <T : JsonWebToken> parse(
            str: String,
            clazz: KClass<T>,
        ): T? {
            val parts = str.split(".")
            if (parts.size < 2 || parts.size > 3) return null

            val jwt = JsonUtil.unmarshal(Base64UrlUtil.decode(parts[1]), clazz.java)
            jwt.header = JsonUtil.unmarshal(Base64UrlUtil.decode(parts[0]), Header::class.java)
            jwt.payload = Base64UrlUtil.decode(parts[1])
            jwt.signature = Base64UrlUtil.decode(parts[2])
            jwt.tokenString = str

            return jwt
        }
    }

    fun getPayloadJSON(): String {
        val payload = this.payload ?: return ""
        val obj = JsonUtil.unmarshal(payload, Any::class.java)
        return JsonUtil.marshal(obj)
    }

    fun getExpDate(): Date {
        return Date(this.exp * 1000L)
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class Header(var kid: String? = null, var alg: String? = null)
}
