package com.example.demo.repository

import com.example.demo.domain.*
import org.springframework.data.domain.*
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FeedbackRepository : JpaRepository<Feedback, UUID> {
    fun findByUser(user: User, pageable: Pageable): Page<Feedback>

    override fun findAll(pageable: Pageable): Page<Feedback>

    fun existsByUserAndChat(user: User, chat: Chat): Boolean
}
