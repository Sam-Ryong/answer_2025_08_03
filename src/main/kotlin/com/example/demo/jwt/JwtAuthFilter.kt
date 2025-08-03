package com.example.demo.jwt

import com.example.demo.auth.AuthenticatedUser
import com.example.demo.repository.UserRepository
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.hibernate.annotations.Filter
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtTokenGenerator: JwtTokenGenerator,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = resolveToken(request.getHeader("Authorization"))

        if (token != null && jwtTokenGenerator.validateToken(token)) {
            val email = jwtTokenGenerator.getEmail(token)

            val userDetails = userDetailsService.loadUserByUsername(email)

            val auth = UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.authorities
            ).apply {
                details = WebAuthenticationDetailsSource().buildDetails(request)
            }

            SecurityContextHolder.getContext().authentication = auth
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(bearer: String?): String? {
        return if (bearer != null && bearer.startsWith("Bearer ")) {
            bearer.removePrefix("Bearer ").trim()
        } else null
    }
}
