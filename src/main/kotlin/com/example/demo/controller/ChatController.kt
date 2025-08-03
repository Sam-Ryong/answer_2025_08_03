package com.example.demo.controller

import com.example.demo.auth.AuthenticatedUser
import com.example.demo.domain.Chat
import com.example.demo.domain.User
import com.example.demo.service.chat.ChatService
import com.example.demo.service.chat.ChatService.CreateChatRequest
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.thread

@RestController
@RequestMapping("/api/chats")
class ChatController(
    private val chatService: ChatService
) {

    @PostMapping("/question", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun question(
        @AuthenticationPrincipal authUser: AuthenticatedUser,
        @RequestBody request: QuestionRequest
    ): ResponseEntity<Any> {
        return if (request.isStreaming) {
            val emitter = ResponseBodyEmitter()

            // 비동기 처리
            thread {
                try {
                    val userEmail = authUser.email
                    val chat = chatService.createStreamChat(
                        CreateChatRequest(
                            email = userEmail,
                            question = request.question,
                            isStreaming = true,
                            model = request.model
                        )
                    )

                    emitter.complete()
                } catch (e: Exception) {
                    emitter.completeWithError(e)
                }
            }

            ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(emitter)
        } else {
            val chat = chatService.createChat(
                CreateChatRequest(
                    email = authUser.email,
                    question = request.question,
                    isStreaming = false,
                    model = request.model
                )
            )
            ResponseEntity.ok(chat)
        }
    }

    @GetMapping
    fun getAllChats(
        @AuthenticationPrincipal authUser: AuthenticatedUser,
        @RequestParam(defaultValue = "false") sortAsc: Boolean,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<ThreadResponse>> {
        val chatsByThread = chatService.getAllChat(

                email = authUser.email,
                isAdmin = authUser.role.equals("admin", ignoreCase = true),
                sortAsc = sortAsc,
                page = page,
                size = size

        )
        return ResponseEntity.ok(chatsByThread)
    }

    data class QuestionRequest(
        val question: String,
        val isStreaming: Boolean = false,
        val model: String? = null
    )


    data class ChatResponse(
        val id: UUID,
        val question: String,
        val answer: String,
        val createdAt: LocalDateTime
    )

    data class ThreadResponse(
        val id: UUID,
        val createdAt: LocalDateTime,
        val chats: List<ChatResponse>
    )
}
