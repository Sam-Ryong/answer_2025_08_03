package com.example.demo.repository

import com.example.demo.domain.Thread
import com.example.demo.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.JpaRepository
import java.awt.print.Pageable
import java.util.*

interface ThreadRepository : JpaRepository<Thread, UUID> {
    fun findTopByUserOrderByCreatedAtDesc(user: User): Thread?

    fun findByUser(user: User, pageable: org.springframework.data.domain.Pageable): Page<Thread>
}
