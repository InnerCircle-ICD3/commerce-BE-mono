package com.fastcampus.commerce.chat.application

import com.fastcampus.commerce.chat.domain.entity.ChatRoom
import com.fastcampus.commerce.chat.domain.entity.ChatRoomStatus
import com.fastcampus.commerce.chat.domain.entity.SenderType
import com.fastcampus.commerce.chat.domain.error.ChatErrorCode
import com.fastcampus.commerce.chat.infrastructure.repository.ChatMessageRepository
import com.fastcampus.commerce.chat.infrastructure.repository.ChatRoomRepository
import com.fastcampus.commerce.chat.interfaces.ChatMessageRequest
import com.fastcampus.commerce.chat.interfaces.ChatMessageResponse
import com.fastcampus.commerce.chat.interfaces.ChatRoomResponse
import com.fastcampus.commerce.chat.interfaces.CreateChatRoomRequest
import com.fastcampus.commerce.common.error.CoreException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChatService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val chatMessageService: ChatMessageService,
) {
    @Transactional
    fun createChatRoom(request: CreateChatRoomRequest): ChatRoomResponse {
        // senderType에 따라 userId/guestId 설정
        val userId = if (request.senderType == SenderType.USER) request.senderId.toLongOrNull() else null
        val guestId = if (request.senderType == SenderType.GUEST) request.senderId else null

        // 기존 활성 채팅방 확인
        val existingRoom = when {
            userId != null && request.productId != null ->
                chatRoomRepository.findActiveByUserIdAndProductId(userId, request.productId)
            guestId != null && request.productId != null ->
                chatRoomRepository.findActiveByGuestIdAndProductId(guestId, request.productId)
            else -> null
        }

        // 기존 채팅방이 있으면 반환
        if (existingRoom != null) {
            // 초기 메시지가 있으면 전송
            if (!request.initialMessage.isNullOrBlank()) {
                sendInitialMessage(existingRoom.id!!, request)
            }
            return toChatRoomResponse(existingRoom)
        }

        // 새 채팅방 생성
        val chatRoom = ChatRoom(
            guestId = guestId,
            userId = userId,
            productId = request.productId,
            status = ChatRoomStatus.REQUESTED,
        )

        val savedRoom = chatRoomRepository.save(chatRoom)

        // 초기 메시지가 있으면 전송
        if (!request.initialMessage.isNullOrBlank()) {
            sendInitialMessage(savedRoom.id!!, request)
        }

        return toChatRoomResponse(savedRoom)
    }

    fun getChatRoomList(userId: Long? = null, guestId: String? = null): List<ChatRoomResponse> {
        val chatRooms = when {
            userId != null -> chatRoomRepository.findByUserIdOrderByCreatedAtDesc(userId)
            guestId != null -> chatRoomRepository.findByGuestIdOrderByCreatedAtDesc(guestId)
            else -> throw CoreException(ChatErrorCode.BAD_REQUEST, "userId 또는 guestId가 필요합니다.")
        }

        // 각 채팅방의 마지막 메시지 조회
        val roomIds = chatRooms.mapNotNull { it.id }
        val lastMessages = if (roomIds.isNotEmpty()) {
            chatMessageRepository.findLastMessagesByChatRoomIds(roomIds)
                .associateBy { it.chatRoomId }
        } else {
            emptyMap()
        }

        return chatRooms.map { room ->
            val lastMessage = lastMessages[room.id]
            toChatRoomResponse(room, lastMessage)
        }
    }

    fun getChatRoomDetail(roomId: Long): ChatRoomResponse {
        val chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow { CoreException(ChatErrorCode.NOT_FOUND, "채팅방을 찾을 수 없습니다.") }

        val lastMessage = chatMessageRepository.findTopByChatRoomIdOrderByCreatedAtDesc(roomId)
        return toChatRoomResponse(chatRoom, lastMessage)
    }

    fun getChatMessages(roomId: Long, pageable: Pageable): Page<ChatMessageResponse> {
        // 채팅방 존재 확인
        if (!chatRoomRepository.existsById(roomId)) {
            throw CoreException(ChatErrorCode.NOT_FOUND, "채팅방을 찾을 수 없습니다.")
        }

        return chatMessageRepository.findByChatRoomIdOrderByCreatedAtDesc(roomId, pageable)
            .map { message ->
                ChatMessageResponse(
                    id = message.id!!,
                    chatRoomId = message.chatRoomId,
                    content = message.content,
                    senderType = message.senderType,
                    senderId = message.senderId,
                    senderName = getSenderName(message.senderType),
                    createdAt = message.createdAt,
                    productInfo = null, // TODO: 상품 정보 연동 필요
                )
            }
    }

    @Transactional
    fun updateChatRoomStatus(roomId: Long, status: String): ChatRoomResponse {
        val chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow { CoreException(ChatErrorCode.NOT_FOUND, "채팅방을 찾을 수 없습니다.") }

        val newStatus = try {
            ChatRoomStatus.valueOf(status.uppercase())
        } catch (e: IllegalArgumentException) {
            throw CoreException(ChatErrorCode.BAD_REQUEST, "유효하지 않은 상태값입니다.")
        }

        // 상태 변경 가능 여부 확인
        if (!isValidStatusTransition(chatRoom.status, newStatus)) {
            throw CoreException(
                ChatErrorCode.BAD_REQUEST,
                "현재 상태(${chatRoom.status})에서 ${newStatus}로 변경할 수 없습니다.",
            )
        }

        // status 필드 업데이트
        chatRoom.changeStatus(newStatus)

        val updatedRoom = chatRoomRepository.save(chatRoom)
        return toChatRoomResponse(updatedRoom)
    }

    @Transactional
    fun assignAdminToChatRoom(roomId: Long, adminId: Long): ChatRoomResponse {
        val chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow { CoreException(ChatErrorCode.NOT_FOUND, "채팅방을 찾을 수 없습니다.") }

        if (chatRoom.adminId != null) {
            throw CoreException(ChatErrorCode.BAD_REQUEST, "이미 관리자가 배정된 채팅방입니다.")
        }

        chatRoom.assignAdmin(adminId)

        val updatedRoom = chatRoomRepository.save(chatRoom)
        return toChatRoomResponse(updatedRoom)
    }

    // Helper methods
    private fun toChatRoomResponse(
        chatRoom: ChatRoom,
        lastMessage: com.fastcampus.commerce.chat.domain.entity.ChatMessage? = null,
    ): ChatRoomResponse {
        return ChatRoomResponse(
            id = chatRoom.id!!,
            guestId = chatRoom.guestId,
            userId = chatRoom.userId,
            adminId = chatRoom.adminId,
            productId = chatRoom.productId,
            status = chatRoom.status.name,
            createdAt = chatRoom.createdAt,
            lastMessage = lastMessage?.content,
            lastMessageAt = lastMessage?.createdAt,
        )
    }

    private fun getSenderName(senderType: com.fastcampus.commerce.chat.domain.entity.SenderType): String {
        return when (senderType) {
            com.fastcampus.commerce.chat.domain.entity.SenderType.GUEST -> "고객"
            com.fastcampus.commerce.chat.domain.entity.SenderType.USER -> "회원"
            com.fastcampus.commerce.chat.domain.entity.SenderType.ADMIN -> "상담사"
        }
    }

    private fun isValidStatusTransition(current: ChatRoomStatus, next: ChatRoomStatus): Boolean {
        return when (current) {
            ChatRoomStatus.REQUESTED -> next in listOf(ChatRoomStatus.ON_CHAT, ChatRoomStatus.END)
            ChatRoomStatus.ON_CHAT -> next in listOf(ChatRoomStatus.AWAITING, ChatRoomStatus.END)
            ChatRoomStatus.AWAITING -> next in listOf(ChatRoomStatus.ON_CHAT, ChatRoomStatus.END)
            ChatRoomStatus.END -> false // 종료된 채팅방은 상태 변경 불가
        }
    }

    private fun sendInitialMessage(roomId: Long, request: CreateChatRoomRequest) {
        val messageRequest = ChatMessageRequest(
            chatRoomId = roomId,
            content = request.initialMessage!!,
            senderType = request.senderType,
            senderId = request.senderId,
        )

        chatMessageService.saveAndSendMessage(messageRequest)
    }
}
