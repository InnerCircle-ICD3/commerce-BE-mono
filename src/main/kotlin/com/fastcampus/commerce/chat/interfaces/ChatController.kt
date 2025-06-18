package com.fastcampus.commerce.chat.interfaces

import com.fastcampus.commerce.chat.application.ChatService
import com.fastcampus.commerce.chat.application.ChatMessageService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chat")
class ChatController(
    private val chatService: ChatService,
    private val chatMessageService: ChatMessageService
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
        @PageableDefault(size = 20, sort = ["createdAt"], direction = org.springframework.data.domain.Sort.Direction.DESC) pageable: Pageable
    ): Page<ChatMessageResponse> {
        return chatService.getChatMessages(roomId, pageable)
    }

    // 채팅방 상태 변경 (관리자 권한 필요)
    @PatchMapping("/rooms/{roomId}/status")
    fun updateChatRoomStatus(
        @PathVariable roomId: Long,
        @RequestBody request: UpdateChatRoomStatusRequest
    ): ChatRoomResponse {
        // 상태가 END인 경우 chatMessageService의 endChat 호출
        if (request.status == "END") {
            chatMessageService.endChat(roomId)
        }
        return chatService.updateChatRoomStatus(roomId, request.status)
    }

    // 관리자 채팅방 입장
    @PostMapping("/rooms/{roomId}/admin-join")
    fun adminJoinChatRoom(
        @PathVariable roomId: Long,
        @RequestBody request: AdminJoinRequest
    ): Map<String, String> {
        chatMessageService.handleAdminJoin(roomId, request.adminId)
        return mapOf("message" to "관리자가 채팅방에 입장했습니다.")
    }

}
