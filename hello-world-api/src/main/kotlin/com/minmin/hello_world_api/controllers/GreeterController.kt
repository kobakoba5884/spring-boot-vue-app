package com.minmin.hello_world_api.controllers

import com.minmin.hello_world_api.models.HelloResponse
import com.minmin.hello_world_api.services.GreeterService
import com.minmin.hello_world_api.services.MessageService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/greeter")
class GreeterController(
        private val greeterService: GreeterService,
        @Qualifier("EnglishMessageServiceImpl") 
        private val messageService: MessageService
) {

    @GetMapping("/hello/byservice/{name}")
    fun helloByService(@PathVariable("name") name: String): HelloResponse {
        val message = greeterService.sayHello(name)

        return HelloResponse(message)
    }
}
