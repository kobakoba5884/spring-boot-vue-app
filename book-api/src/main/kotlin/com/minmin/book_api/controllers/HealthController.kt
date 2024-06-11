package com.minmin.book_api.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/health")
class HealthController {
    @GetMapping("")
    fun getHealth(): ResponseEntity<String> {

        return ResponseEntity.ok("Book API is up and runinning!!")
    }
}
