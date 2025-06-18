package com.fastcampus.commerce.chat.domain.entity

import com.fastcampus.commerce.common.entity.BaseEntity
import java.time.LocalDateTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "chat_messages")
@Entity
class ChatMessage(
    @Column(name = "chat_room_id", nullable = false)
    val chatRoomId: Long,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val senderType: SenderType,
    @Column(name = "sender_id", nullable = false)
    val senderId: String,
    @Column(nullable = false)
    val content: String,
) : BaseEntity(){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column
    var deletedAt: LocalDateTime? = null

    fun getSenderIdAsLong(): Long? = 
        when (senderType) {
            SenderType.USER, SenderType.ADMIN -> senderId.toLongOrNull()
            SenderType.GUEST -> null
        }

    companion object {
        fun createGuestMessage(chatRoomId: Long, guestId: String, content: String): ChatMessage =
            ChatMessage(
                chatRoomId = chatRoomId,
                senderType = SenderType.GUEST,
                senderId = guestId,
                content = content
            )

        fun createUserMessage(chatRoomId: Long, userId: Long, content: String): ChatMessage =
            ChatMessage(
                chatRoomId = chatRoomId,
                senderType = SenderType.USER,
                senderId = userId.toString(),
                content = content
            )

        fun createAdminMessage(chatRoomId: Long, adminId: Long, content: String): ChatMessage =
            ChatMessage(
                chatRoomId = chatRoomId,
                senderType = SenderType.ADMIN,
                senderId = adminId.toString(),
                content = content
            )
    }
}
