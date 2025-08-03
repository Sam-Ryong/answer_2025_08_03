package com.example.demo.service.feedback

import com.example.demo.domain.*
import com.example.demo.repository.*
import org.springframework.data.domain.*
import org.springframework.stereotype.Service
import java.util.*

@Service
class FeedbackServiceImpl(
    private val feedbackRepository: FeedbackRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) : FeedbackService {

    override fun createFeedback(request: FeedbackService.CreateFeedbackRequest): Feedback {
        val user = userRepository.findByEmail(request.userEmail)
            ?: throw IllegalArgumentException("유저를 찾을 수 없습니다.")
        val chat = chatRepository.findById(UUID.fromString(request.chatId))
            .orElseThrow { IllegalArgumentException("대화를 찾을 수 없습니다.") }

        if (user.role != "admin" && chat.thread.user.id != user.id) {
            throw IllegalAccessException("본인의 대화에만 피드백을 남길 수 있습니다.")
        }

        if (feedbackRepository.existsByUserAndChat(user, chat)) {
            throw IllegalStateException("이미 피드백을 남겼습니다.")
        }

        val feedback = Feedback(
            user = user,
            chat = chat,
            isPositive = request.isPositive
        )

        return feedbackRepository.save(feedback)
    }

    override fun getFeedbacks(request: FeedbackService.GetFeedbacksRequest): Page<Feedback> {
        val user = userRepository.findByEmail(request.userEmail)
            ?: throw IllegalArgumentException("유저를 찾을 수 없습니다.")

        val pageable = PageRequest.of(
            request.page,
            request.size,
            if (request.sortAsc) Sort.by("createdAt").ascending() else Sort.by("createdAt").descending()
        )

        val basePage = if (request.isAdmin) {
            feedbackRepository.findAll(pageable)
        } else {
            feedbackRepository.findByUser(user, pageable)
        }

        return if (request.isPositive != null) {
            val filtered = basePage.content.filter { it.isPositive == request.isPositive }
            PageImpl(filtered, pageable, filtered.size.toLong())
        } else {
            basePage
        }
    }


    override fun updateStatus(id: String, newStatus: FeedbackStatus): Feedback {
        val feedback = feedbackRepository.findById(UUID.fromString(id))
            .orElseThrow { IllegalArgumentException("피드백을 찾을 수 없습니다.") }

        feedback.status = newStatus
        return feedbackRepository.save(feedback)
    }
}
