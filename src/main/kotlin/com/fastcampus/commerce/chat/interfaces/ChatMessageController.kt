package com.fastcampus.commerce.chat.interfaces

import com.fastcampus.commerce.chat.application.ChatMessageService
import org.springframework.messaging.handler.annotation.DestinationVariable
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

    @MessageMapping("/chat/admin-join/{roomId}")
    fun adminJoin(@DestinationVariable roomId: Long, @Payload request: AdminJoinRequest) {
        chatMessageService.handleAdminJoin(roomId, request.adminId)
    }

    @MessageMapping("/chat/end")
    fun endChat(@Payload request: EndChatRequest) {
        chatMessageService.endChat(request.roomId)
    }
}
