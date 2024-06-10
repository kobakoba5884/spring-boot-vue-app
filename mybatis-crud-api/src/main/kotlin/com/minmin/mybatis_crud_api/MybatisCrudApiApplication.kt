package com.minmin.mybatis_crud_api

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@MapperScan("com.minmin.mybatis_crud_api.gen.mappers")
class MybatisCrudApiApplication

fun main(args: Array<String>) {
    runApplication<MybatisCrudApiApplication>(*args)
}
