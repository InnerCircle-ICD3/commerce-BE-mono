package com.fastcampus.commerce.chat.infrastructure.repository

import com.fastcampus.commerce.chat.domain.entity.ChatMessage
import com.fastcampus.commerce.chat.domain.entity.SenderType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ChatMessageRepository : JpaRepository<ChatMessage, Long> {
    // 채팅방 ID로 메시지 조회 (페이징)
    fun findByChatRoomIdOrderByCreatedAtDesc(chatRoomId: Long, pageable: Pageable): Page<ChatMessage>

    // 채팅방 ID로 모든 메시지 조회
    fun findByChatRoomIdOrderByCreatedAt(chatRoomId: Long): List<ChatMessage>

    // 특정 발신자 타입의 메시지 조회
    fun findByChatRoomIdAndSenderType(chatRoomId: Long, senderType: SenderType): List<ChatMessage>

    // 특정 시간 이후의 메시지 조회
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoomId = :chatRoomId AND cm.createdAt > :since ORDER BY cm.createdAt")
    fun findRecentMessages(
        @Param("chatRoomId") chatRoomId: Long,
        @Param("since") since: LocalDateTime,
    ): List<ChatMessage>

    // 채팅방의 마지막 메시지 조회
    fun findTopByChatRoomIdOrderByCreatedAtDesc(chatRoomId: Long): ChatMessage?

    // 채팅방의 메시지 개수 조회
    fun countByChatRoomId(chatRoomId: Long): Long

    // 여러 채팅방의 마지막 메시지 조회
    @Query(
        """
        SELECT cm FROM ChatMessage cm
        WHERE cm.id IN (
            SELECT MAX(cm2.id) FROM ChatMessage cm2
            WHERE cm2.chatRoomId IN :chatRoomIds
            GROUP BY cm2.chatRoomId
        )
    """,
    )
    fun findLastMessagesByChatRoomIds(
        @Param("chatRoomIds") chatRoomIds: List<Long>,
    ): List<ChatMessage>

    // 특정 내용을 포함하는 메시지 검색
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoomId = :chatRoomId AND cm.content LIKE %:keyword% ORDER BY cm.createdAt DESC")
    fun searchMessages(
        @Param("chatRoomId") chatRoomId: Long,
        @Param("keyword") keyword: String,
        pageable: Pageable,
    ): Page<ChatMessage>
}
