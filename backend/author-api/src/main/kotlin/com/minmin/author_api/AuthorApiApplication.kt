package com.minmin.author_api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AuthorApiApplication

fun main(args: Array<String>) {
	runApplication<AuthorApiApplication>(*args)
}
