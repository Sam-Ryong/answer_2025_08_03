package com.example.demo.repository

import com.example.demo.domain.Chat
import com.example.demo.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*
@Repository
interface ChatRepository : JpaRepository<Chat, UUID> {
    fun findAllByThreadUser(user: User, pageable: Pageable): Page<Chat>
}
