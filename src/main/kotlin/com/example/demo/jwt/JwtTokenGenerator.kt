package com.example.demo.jwt

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenGenerator {

    private val secretKey: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val validityInMilliseconds: Long = 3600000 // 1시간

    fun createToken(email: String, role: String): String {
        val now = Date()
        val expiry = Date(now.time + validityInMilliseconds)

        return Jwts.builder()
            .setSubject(email)
            .claim("role", role)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(secretKey)
            .compact()
    }

    fun getEmail(token: String): String =
        Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .subject

    fun validateToken(token: String): Boolean = try {
        Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
        true
    } catch (e: Exception) {
        false
    }
}
