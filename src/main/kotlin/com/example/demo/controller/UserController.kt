package com.example.demo.controller

import com.example.demo.domain.User
import com.example.demo.service.user.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/signup")
    fun signUp(@RequestBody request: UserService.SignUpRequest): ResponseEntity<User> {
        val newUser = userService.signUp(request)
        return ResponseEntity.ok(newUser)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: UserService.LoginRequest): ResponseEntity<Map<String, String>> {
        val token = userService.login(request)
        return ResponseEntity.ok(mapOf("token" to token))
    }


}
