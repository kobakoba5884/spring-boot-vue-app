package com.minmin.authHandsOnClient.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import java.io.IOException

class JsonUtil {
    companion object {
        private val mapper: ObjectMapper =
            ObjectMapper().apply {
                configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                setSerializationInclusion(JsonInclude.Include.NON_NULL)
            }

        fun <T> unmarshal(
            content: ByteArray,
            type: Class<T>,
        ): T {
            return try {
                mapper.readValue(content, type)
            } catch (e: IOException) {
                e.printStackTrace()
                throw RuntimeException()
            }
        }

        fun marshal(obj: Any): String {
            return marshal(obj, true)
        }

        fun marshal(
            obj: Any,
            indent: Boolean,
        ): String {
            return try {
                mapper.configure(SerializationFeature.INDENT_OUTPUT, indent)
                mapper.writeValueAsString(obj)
            } catch (e: IOException) {
                e.printStackTrace()
                ""
            }
        }
    }
}
