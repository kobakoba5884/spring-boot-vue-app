package com.minmin.hello_world_api.controllers

import com.minmin.hello_world_api.models.HelloRequest
import com.minmin.hello_world_api.models.HelloResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class HelloWorldController {

    @GetMapping("/")
    fun hello(): String {
        return "hello world!!!"
    }

    @GetMapping("/hello")
    fun helloWithName(@RequestParam("name") name: String): HelloResponse {
        return HelloResponse("Hello ${name}")
    }

    @GetMapping("/hello/{name}")
    fun hellPathValue(@PathVariable("name") name: String): HelloResponse {
        return HelloResponse("Hello ${name}")
    }

    @PostMapping("/hello")
    fun helloByPost(@RequestBody request: HelloRequest): HelloResponse {
        return HelloResponse("Hello ${request.name}")
    }
}
