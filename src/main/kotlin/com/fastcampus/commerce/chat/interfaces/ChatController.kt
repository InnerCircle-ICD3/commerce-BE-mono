package com.fastcampus.commerce.chat.interfaces

import com.fastcampus.commerce.chat.application.ChatService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chat")
class ChatController(
    private val chatService: ChatService
) {

    // 채팅방 생성
    @PostMapping("/rooms")
    fun createChatRoom(
        @RequestBody request: CreateChatRoomRequest
    ): ChatRoomResponse {
        return chatService.createChatRoom(request)
    }

    // 채팅방 목록 조회
    @GetMapping("/rooms")
    fun getChatRoomList(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) guestId: String?
    ): List<ChatRoomResponse> {
        return chatService.getChatRoomList(userId, guestId)
    }

    // 채팅방 상세 조회
    @GetMapping("/rooms/{roomId}")
    fun getChatRoomDetail(
        @PathVariable roomId: Long
    ): ChatRoomResponse {
        return chatService.getChatRoomDetail(roomId)
    }

    @GetMapping("/rooms/{roomId}/messages")
    fun getChatMessages(
        @PathVariable roomId: Long,
        @PageableDefault(size = 20, sort = ["createdAt"]) pageable: Pageable
    ): Page<ChatMessageResponse> {
        return chatService.getChatMessages(roomId, pageable)
    }

    // 채팅방 상태 변경 (선택적)
    @PatchMapping("/rooms/{roomId}/status")
    fun updateChatRoomStatus(
        @PathVariable roomId: Long,
        @RequestParam status: String
    ): ChatRoomResponse {
        return chatService.updateChatRoomStatus(roomId, status)
    }
}
