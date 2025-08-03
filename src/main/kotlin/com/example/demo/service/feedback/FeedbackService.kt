package com.example.demo.service.feedback

import com.example.demo.domain.Feedback
import com.example.demo.domain.FeedbackStatus
import org.springframework.data.domain.Page

interface FeedbackService {

    fun createFeedback(request: CreateFeedbackRequest): Feedback

    fun getFeedbacks(request: GetFeedbacksRequest): Page<Feedback>

    fun updateStatus(id: String, newStatus: FeedbackStatus): Feedback

    data class CreateFeedbackRequest(
        val userEmail: String,
        val chatId: String,
        val isPositive: Boolean
    )

    data class GetFeedbacksRequest(
        val userEmail: String,
        val isAdmin: Boolean,
        val isPositive: Boolean? = null,
        val sortAsc: Boolean = false,
        val page: Int = 0,
        val size: Int = 20
    )
}
