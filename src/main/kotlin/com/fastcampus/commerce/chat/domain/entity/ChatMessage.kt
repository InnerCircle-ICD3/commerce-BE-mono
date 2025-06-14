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
    @Column(nullable = false)
    val content: String,
) : BaseEntity(){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column
    var deletedAt: LocalDateTime? = null
}
