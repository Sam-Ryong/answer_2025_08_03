package com.example.demo.domain

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "feedbacks", uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "chat_id"])])
data class Feedback(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    val chat: Chat,

    @Column(nullable = false)
    val isPositive: Boolean,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: FeedbackStatus = FeedbackStatus.PENDING
)

enum class FeedbackStatus {
    PENDING, RESOLVED
}
