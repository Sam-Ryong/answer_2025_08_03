package com.example.demo.service.user

import com.example.demo.domain.User
import com.example.demo.jwt.JwtTokenGenerator
import com.example.demo.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val jwtTokenGenerator: JwtTokenGenerator
) : UserService {

    override fun signUp(request: UserService.SignUpRequest): User {


        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("이미 사용 중인 이메일입니다.")
        }

        val user = User(
            email = request.email,
            password = request.password,
            name = request.name,
            createdAt = LocalDateTime.now()
        )

        return userRepository.save(user)
    }

    override fun login(request: UserService.LoginRequest): String {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다.")

        if (user.password != request.password) {
            throw IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다.")
        }


        return jwtTokenGenerator.createToken(user.email, user.role)
    }

    override fun access(token: String): User? {

        if (!jwtTokenGenerator.validateToken(token)) return null
        val email = jwtTokenGenerator.getEmail(token)
        val user = userRepository.findByEmail(email)
        if (user == null){
            throw IllegalArgumentException("jwt 인증 실패")
        }
        else{
            return userRepository.findByEmail(email)
        }
    }


}
