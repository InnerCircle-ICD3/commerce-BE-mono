package com.fastcampus.commerce.chat.infrastructure.repository

import com.fastcampus.commerce.chat.domain.entity.ChatRoom
import com.fastcampus.commerce.chat.domain.entity.ChatRoomStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomRepository : JpaRepository<ChatRoom, Long> {

    // 사용자 ID로 채팅방 목록 조회
    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<ChatRoom>

    // 게스트 ID로 채팅방 목록 조회
    fun findByGuestIdOrderByCreatedAtDesc(guestId: String): List<ChatRoom>

    // 사용자 ID와 상태로 채팅방 조회
    fun findByUserIdAndStatus(userId: Long, status: ChatRoomStatus): List<ChatRoom>

    // 게스트 ID와 상태로 채팅방 조회
    fun findByGuestIdAndStatus(guestId: String, status: ChatRoomStatus): List<ChatRoom>

    // 상품 ID로 채팅방 조회
    fun findByProductId(productId: Long): List<ChatRoom>

    // 관리자 ID로 할당된 채팅방 조회
    fun findByAdminIdOrderByCreatedAtDesc(adminId: Long): List<ChatRoom>

    // 특정 상태의 채팅방 개수 조회
    fun countByStatus(status: ChatRoomStatus): Long

    // 사용자와 상품으로 기존 채팅방 조회
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.userId = :userId AND cr.productId = :productId AND cr.status != :excludeStatus")
    fun findActiveByUserIdAndProductId(
        @Param("userId") userId: Long,
        @Param("productId") productId: Long,
        @Param("excludeStatus") excludeStatus: ChatRoomStatus = ChatRoomStatus.END
    ): ChatRoom?

    // 게스트와 상품으로 기존 채팅방 조회
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.guestId = :guestId AND cr.productId = :productId AND cr.status != :excludeStatus")
    fun findActiveByGuestIdAndProductId(
        @Param("guestId") guestId: String,
        @Param("productId") productId: Long,
        @Param("excludeStatus") excludeStatus: ChatRoomStatus = ChatRoomStatus.END
    ): ChatRoom?

    // 관리자가 배정되지 않은 대기 중인 채팅방 조회
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.adminId IS NULL AND cr.status = :status ORDER BY cr.createdAt ASC")
    fun findUnassignedRooms(@Param("status") status: ChatRoomStatus = ChatRoomStatus.REQUESTED): List<ChatRoom>
}
