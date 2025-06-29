package com.fastcampus.commerce.chat.domain.entity

import com.fastcampus.commerce.common.entity.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@SQLDelete(sql = "update chat_rooms set deleted_at = now() where id = ?")
@SQLRestriction("deleted_at is null")
@Table(name = "chat_rooms")
@Entity
class ChatRoom(
    @Column
    val guestId: String? = null,
    @Column
    val userId: Long? = null,
    @Column
    val productId: Long? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ChatRoomStatus = ChatRoomStatus.REQUESTED,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column
    var adminId: Long? = null

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updatedAt: LocalDateTime

    @Column
    var deletedAt: LocalDateTime? = null

    // 상태 변경 메서드 추가
    fun changeStatus(newStatus: ChatRoomStatus) {
        // 비즈니스 검증 로직을 여기서 수행할 수 있음
        this.status = newStatus
    }

    fun assignAdmin(adminId: Long) {
        this.adminId = adminId
        this.status = ChatRoomStatus.ON_CHAT
    }
}
