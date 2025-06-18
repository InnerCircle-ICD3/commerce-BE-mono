package com.fastcampus.commerce.chat.application

import com.fastcampus.commerce.chat.domain.entity.ChatMessage
import com.fastcampus.commerce.chat.domain.entity.ChatRoomStatus
import com.fastcampus.commerce.chat.domain.entity.NotificationType
import com.fastcampus.commerce.chat.domain.entity.SenderType
import com.fastcampus.commerce.chat.domain.error.ChatErrorCode
import com.fastcampus.commerce.chat.infrastructure.repository.ChatMessageRepository
import com.fastcampus.commerce.chat.infrastructure.repository.ChatRoomRepository
import com.fastcampus.commerce.chat.interfaces.*
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.product.domain.service.ProductReader
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ChatMessageService(
    private val chatMessageRepository: ChatMessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val productReader: ProductReader,
    private val messagingTemplate: SimpMessagingTemplate,
) {

    fun saveAndSendMessage(request: ChatMessageRequest): ChatMessageResponse {
        // 채팅방 확인
        val chatRoom = chatRoomRepository.findById(request.chatRoomId)
            .orElseThrow { CoreException(ChatErrorCode.NOT_FOUND, "채팅방을 찾을 수 없습니다.") }

        // 채팅방 상태 확인
        if (chatRoom.status == ChatRoomStatus.END) {
            throw CoreException(ChatErrorCode.BAD_REQUEST, "종료된 채팅방입니다.")
        }

        // 메시지 저장
        val message = when (request.senderType) {
            SenderType.GUEST ->
                ChatMessage.createGuestMessage(
                    chatRoomId = request.chatRoomId,
                    guestId = request.senderId!!,
                    content = request.content
                )
            SenderType.USER ->
                ChatMessage.createUserMessage(
                    chatRoomId = request.chatRoomId,
                    userId = request.senderId!!.toLong(),
                    content = request.content
                )
            SenderType.ADMIN ->
                ChatMessage.createAdminMessage(
                    chatRoomId = request.chatRoomId,
                    adminId = request.senderId!!.toLong(),
                    content = request.content
                )
        }

        val savedMessage = chatMessageRepository.save(message)

        // 응답 생성
        val response = ChatMessageResponse(
            id = savedMessage.id!!,
            chatRoomId = savedMessage.chatRoomId,
            content = savedMessage.content,
            senderType = savedMessage.senderType,
            senderId = savedMessage.senderId,
            senderName = getSenderName(savedMessage.senderType),
            createdAt = savedMessage.createdAt,
            productInfo = getProductInfoIfNeeded(request.content, chatRoom.productId)
        )

        // 채팅방 상태 업데이트 (AWAITING -> ON_CHAT)
        if (chatRoom.status == ChatRoomStatus.AWAITING) {
            updateChatRoomStatus(chatRoom, ChatRoomStatus.ON_CHAT)
        }

        return response
    }

    fun sendNotification(roomId: Long, type: NotificationType, message: String) {
        val notification = ChatNotification(
            chatRoomId = roomId,
            type = type,
            message = message,
            timestamp = LocalDateTime.now()
        )

        messagingTemplate.convertAndSend("/sub/chat/room/$roomId", notification)
    }

    // 관리자가 채팅방에 입장할 때
    fun handleAdminJoin(roomId: Long, adminId: Long) {
        val chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow { CoreException(ChatErrorCode.NOT_FOUND, "채팅방을 찾을 수 없습니다.") }

        // 관리자 배정
        if (chatRoom.adminId == null) {
            chatRoom.assignAdmin(adminId) // 엔터티의 메서드를 사용하여 관리자를 지정하고 상태를 업데이트합니다.

            chatRoomRepository.save(chatRoom) // 업데이트된 chatRoom 엔티티를 한 번 저장합니다.
        }

        // 입장 알림 전송
        sendNotification(roomId, NotificationType.ADMIN_ASSIGNED, "관리자가 입장했습니다.")
    }

    // 채팅 종료
    fun endChat(roomId: Long) {
        val chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow { CoreException(ChatErrorCode.NOT_FOUND, "채팅방을 찾을 수 없습니다.") }

        updateChatRoomStatus(chatRoom, ChatRoomStatus.END)

        // 종료 알림 전송
        sendNotification(roomId, NotificationType.CHAT_ENDED, "채팅이 종료되었습니다.")
    }

    // Helper methods
    private fun getSenderName(senderType: SenderType): String {
        return when (senderType) {
            SenderType.GUEST -> "비회원"
            SenderType.USER -> "회원"
            SenderType.ADMIN -> "관리자"
        }
    }

    private fun getProductInfoIfNeeded(content: String, productId: Long?): ProductInfo? {
        // 상품 ID가 메시지에 포함되어 있거나 채팅방에 연결된 경우
        if (productId != null && content.contains("#product")) {
            return try {
                val product = productReader.getProductById(productId)
                ProductInfo(
                    productId = product.id!!,
                    productName = product.name,
                    thumbnailUrl = product.thumbnail
                )
            } catch (e: Exception) {
                null
            }
        }
        return null
    }

    private fun updateChatRoomStatus(chatRoom: com.fastcampus.commerce.chat.domain.entity.ChatRoom, newStatus: ChatRoomStatus) {
        chatRoom.changeStatus(newStatus) // Use the entity's method to change status

        chatRoomRepository.save(chatRoom)
    }
}
