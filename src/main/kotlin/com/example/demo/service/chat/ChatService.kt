package com.example.demo.service.chat

import com.example.demo.controller.ChatController
import com.example.demo.domain.Chat
import org.springframework.data.domain.Page
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter
import java.util.*

interface ChatService {

    fun createChat(request: CreateChatRequest): String

    fun createStreamChat(request: CreateChatRequest): ResponseBodyEmitter

    fun getAllChat( email: String,
                    isAdmin: Boolean,
                    sortAsc: Boolean,
                    page: Int,
                    size: Int): List<ChatController.ThreadResponse>

    fun deleteThread(request: DeleteThreadRequest): UUID

    data class CreateChatRequest(
        val email: String,
        val question: String,
        val isStreaming: Boolean = false,
        val model: String? = null // 모델이름 넣어야뎀~!
    )


    data class GetAllChatRequest(
        val email: String,
        val isAdmin: Boolean,
        val sortAsc: Boolean,
        val page: Int,
        val size: Int
    )

    data class DeleteThreadRequest(
        val email: String,
        val tid: UUID,
    )
}