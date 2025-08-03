package com.example.demo.service.chat

import com.example.demo.controller.ChatController
import com.example.demo.domain.Chat
import com.example.demo.domain.Thread
import com.example.demo.repository.ChatRepository
import com.example.demo.repository.ThreadRepository
import com.example.demo.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter
import java.awt.print.Pageable
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.thread

@Service
class ChatServiceImpl(
    private val userRepository: UserRepository,
    private val threadRepository: ThreadRepository,
    private val chatRepository: ChatRepository
) : ChatService {

    override fun createChat(request: ChatService.CreateChatRequest): String {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("해당 이메일의 유저가 존재하지 않습니다.")

        val now = LocalDateTime.now()
        val latestThread = threadRepository.findTopByUserOrderByCreatedAtDesc(user)

        val thread = if (latestThread == null ||
            latestThread.chats.maxByOrNull { it.createdAt ?: now }?.createdAt?.isBefore(now.minusMinutes(30)) != false
        ) {
            threadRepository.save(Thread(user = user))
        } else {
            latestThread
        }

        val answer = generateAnswer(request.question, request.model)

        val chat = Chat(
            thread = thread,
            question = request.question,
            answer = answer
        )

        chatRepository.save(chat)

        return answer
    }

    override fun createStreamChat(request: ChatService.CreateChatRequest, emitter: ResponseBodyEmitter) {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("해당 이메일의 유저가 존재하지 않습니다.")

        val now = LocalDateTime.now()

        val latestThread = threadRepository.findTopByUserOrderByCreatedAtDesc(user)

        val thread = if (latestThread == null ||
            latestThread.chats.maxByOrNull { it.createdAt ?: now }?.createdAt?.isBefore(now.minusMinutes(30)) != false
        ) {
            threadRepository.save(Thread(user = user))
        } else {
            latestThread
        }

        thread {
            try {
                val chunks = generateStreamingAnswer(request.question, request.model)
                val answerBuilder = StringBuilder()

                chunks.forEach { chunk ->
                    emitter.send(chunk + "\n")
                    answerBuilder.appendLine(chunk)
                }

                val chat = Chat(
                    thread = thread,
                    question = request.question,
                    answer = answerBuilder.toString()
                )
                chatRepository.save(chat)

                emitter.complete()
            } catch (e: Exception) {
                emitter.completeWithError(e)
            }
        }
    }




    override fun getAllChat(
        email: String,
        isAdmin: Boolean,
        sortAsc: Boolean,
        page: Int,
        size: Int
    ): List<ChatController.ThreadResponse> {
        val pageable: org.springframework.data.domain.Pageable = PageRequest.of(
            page,
            size,
            if (sortAsc) Sort.by("createdAt").ascending() else Sort.by("createdAt").descending()
        )


        val threads = if (isAdmin) {
            threadRepository.findAll(pageable)
        } else {
            val user = userRepository.findByEmail(email)
                ?: throw IllegalArgumentException("유저를 찾을 수 없습니다.")
            threadRepository.findByUser(user, pageable)
        }

        return threads.content.map { thread ->
            val chatDtos = thread.chats.sortedBy { it.createdAt }.map { chat ->
                ChatController.ChatResponse(
                    id = chat.id!!,
                    question = chat.question,
                    answer = chat.answer,
                    createdAt = chat.createdAt!!
                )
            }

            ChatController.ThreadResponse(
                id = thread.id!!,
                createdAt = thread.createdAt!!,
                chats = chatDtos
            )
        }
    }


    override fun deleteThread(request: ChatService.DeleteThreadRequest) : UUID {

        val thread = threadRepository.findById(request.tid)
            .orElseThrow { IllegalArgumentException("해당 thread가 존재하지 않습니다.") }

        threadRepository.delete(thread)
        return request.tid
    }

    // todo 해야뎀
    private fun generateAnswer(question: String, model: String?): String {
        return "응답 (모델: ${model ?: "기본"}): '$question'에 대한 답변입니다."
    }

    private fun generateStreamingAnswer(question: String, model: String?): List<String> {
        return listOf(
            "STREAM 응답 시작 (모델: ${model ?: "기본"})...",
            "질문: '$question'",
            "AI가 생각 중...",
            "첫 번째 문장입니다.",
            "두 번째 문장입니다.",
            "세 번째 문장입니다.",
            "STREAM 종료."
        )
    }

}
