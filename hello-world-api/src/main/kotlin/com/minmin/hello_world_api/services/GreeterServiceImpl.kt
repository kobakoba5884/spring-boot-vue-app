package com.minmin.hello_world_api.controllers

import com.minmin.hello_world_api.services.GreeterService
import org.springframework.stereotype.Service

@Service
class GreeterServiceImpl : GreeterService {

    override fun sayHello(name: String) = "Hello $name"
}
