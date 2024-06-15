package com.minmin.mybatis_crud_api.controllers

import com.minmin.mybatis_crud_api.gen.entities.MyUser
import com.minmin.mybatis_crud_api.gen.mappers.MyUserMapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class UserController(private val myUserMapper: MyUserMapper) {
    @GetMapping("/users/{id}")
    fun getUser(@PathVariable("id") userId: Int): MyUser {
        return myUserMapper.selectByPrimaryKey(userId).get()
    }
}
