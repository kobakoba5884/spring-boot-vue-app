package com.minmin.authHandsonApi.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("token-introspection")
class TokenIntrospectionController {
    @GetMapping("/health")
    fun getHealth(): ResponseEntity<String> {
        return ResponseEntity.ok("TokenIntrospection Controller is up and runinning!!")
    }
}
