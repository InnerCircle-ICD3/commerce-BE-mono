package com.fastcampus.commerce.chat.interfaces

import com.fastcampus.commerce.chat.domain.entity.NotificationType
import com.fastcampus.commerce.chat.domain.entity.SenderType
import java.time.LocalDateTime

// 클라이언트 → 서버 메시지 전송 요청
data class ChatMessageRequest(
    val chatRoomId: Long,
    val content: String,
    val senderType: SenderType,
    val senderId: String? = null,  // guestId or userId or adminId
)

// 서버 → 클라이언트 메시지 응답
data class ChatMessageResponse(
    val id: Long,
    val chatRoomId: Long,
    val content: String,
    val senderType: SenderType,
    val senderId: String? = null,
    val senderName: String? = null,  // 표시할 이름
    val createdAt: LocalDateTime,
    val productInfo: ProductInfo? = null,  // 상품 관련 메시지인 경우
)

// 상품 정보 DTO (채팅에서 상품 공유 시)
data class ProductInfo(
    val productId: Long,
    val productName: String,
    val thumbnailUrl: String? = null,
)

// 채팅방 입장/퇴장 알림
data class ChatNotification(
    val chatRoomId: Long,
    val type: NotificationType,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
)

// 채팅방 생성 요청 DTO
data class CreateChatRoomRequest(
    val senderType: SenderType,
    val senderId: String,  // guestId (for GUEST) or userId (for USER)
    val productId: Long? = null,
    val initialMessage: String? = null,  // 첫 메시지 (옵션)
)

// 채팅방 정보 응답 DTO
data class ChatRoomResponse(
    val id: Long,
    val guestId: String? = null,
    val userId: Long? = null,
    val adminId: Long? = null,
    val productId: Long? = null,
    val status: String,
    val createdAt: LocalDateTime,
    val lastMessage: String? = null,
    val lastMessageAt: LocalDateTime? = null,
)
