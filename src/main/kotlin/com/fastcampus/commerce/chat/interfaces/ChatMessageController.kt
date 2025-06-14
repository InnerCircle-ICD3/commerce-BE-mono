package com.fastcampus.commerce.chat.interfaces

import com.fastcampus.commerce.chat.application.ChatMessageService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class ChatMessageController(
    private val chatMessageService: ChatMessageService,
    private val messagingTemplate: SimpMessagingTemplate
) {

    @MessageMapping("/chat/send")
    fun sendMessage(@Payload request: ChatMessageRequest) {
        val response = chatMessageService.saveAndSendMessage(request)

        messagingTemplate.convertAndSend("/sub/chat/room/${request.chatRoomId}",
            response)
    }
}
