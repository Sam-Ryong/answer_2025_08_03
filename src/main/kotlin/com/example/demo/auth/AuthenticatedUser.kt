package com.example.demo.auth

import com.example.demo.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class AuthenticatedUser(
    private val user: User
) : UserDetails {

    fun getUser(): User = user
    val id: String get() = user.id?.toString() ?: ""
    val email: String get() = user.email
    val role: String get() = user.role

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_${user.role.uppercase()}"))

    override fun getPassword(): String = user.password
    override fun getUsername(): String = user.email
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}
