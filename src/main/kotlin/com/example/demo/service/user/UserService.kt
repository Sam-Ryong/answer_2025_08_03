package com.example.demo.service.user

import com.example.demo.domain.User


interface UserService {


    fun signUp(request: SignUpRequest): User

    fun login(request: LoginRequest): String // jwt 스트링

    fun access(token: String): User? // 인증된 사용자 조회

    data class SignUpRequest(
        val email: String,
        val password: String,
        val name: String,
    )

    data class LoginRequest(
        val email: String,
        val password: String
    )

}