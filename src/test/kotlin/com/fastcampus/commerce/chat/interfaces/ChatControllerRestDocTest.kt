package com.fastcampus.commerce.chat.interfaces

import com.fastcampus.commerce.chat.application.ChatService
import com.fastcampus.commerce.chat.application.ChatMessageService
import com.fastcampus.commerce.chat.domain.entity.SenderType
import com.fastcampus.commerce.config.TestConfig
import com.fastcampus.commerce.restdoc.documentation
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.just
import io.mockk.Runs
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@WebMvcTest(ChatController::class)
@Import(TestConfig::class)
class ChatControllerRestDocTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var chatService: ChatService

    @MockkBean
    lateinit var chatMessageService: ChatMessageService

    val tag = "Chat"
    val privateResource = true

    // 날짜 포맷터 - 실제 응답과 동일한 형식으로
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

    init {
        beforeSpec {
            RestAssuredMockMvc.mockMvc(mockMvc)
        }

        describe("POST /chat/rooms - 채팅방 생성") {
            val summary = "새로운 채팅방을 생성하거나 기존 채팅방을 반환한다."

            it("게스트가 상품 문의 채팅방을 생성할 수 있다") {
                val request = CreateChatRoomRequest(
                    senderType = SenderType.GUEST,
                    senderId = "guest-123",
                    productId = 1L,
                    initialMessage = "이 상품 재고 있나요?"
                )

                val now = LocalDateTime.now()
                val response = ChatRoomResponse(
                    id = 1L,
                    guestId = "guest-123",
                    userId = null,
                    adminId = null,
                    productId = 1L,
                    status = "REQUESTED",
                    createdAt = now,
                    lastMessage = "이 상품 재고 있나요?",
                    lastMessageAt = now
                )

                every { chatService.createChatRoom(request) } returns response

                documentation(
                    identifier = "채팅방_생성_게스트_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = false,
                ) {
                    requestLine(HttpMethod.POST, "/chat/rooms")

                    requestBody {
                        field("senderType", "발신자 타입 (GUEST, USER, ADMIN)", "GUEST")
                        field("senderId", "발신자 ID (게스트ID 또는 유저ID)", "guest-123")
                        field("productId", "상품 ID (선택사항)", 1L)
                        field("initialMessage", "초기 메시지 (선택사항)", "이 상품 재고 있나요?")
                    }

                    responseBody {
                        field("data.id", "채팅방 ID", 1)
                        field("data.guestId", "게스트 ID", "guest-123")
                        optionalField("data.userId", "유저 ID (회원인 경우)", null)
                        optionalField("data.adminId", "관리자 ID (배정된 경우)", null)
                        field("data.productId", "상품 ID", 1)
                        field("data.status", "채팅방 상태", "REQUESTED")
                        field("data.createdAt", "생성 시간", now.format(dateFormatter))
                        field("data.lastMessage", "마지막 메시지", "이 상품 재고 있나요?")
                        field("data.lastMessageAt", "마지막 메시지 시간", now.format(dateFormatter))
                        optionalField("error", "에러 정보", null)
                    }
                }
            }

            it("회원이 상품 문의 채팅방을 생성할 수 있다") {
                val request = CreateChatRoomRequest(
                    senderType = SenderType.USER,
                    senderId = "123",
                    productId = 1L,
                    initialMessage = "상품 구매 문의드립니다"
                )

                val now = LocalDateTime.now()
                val response = ChatRoomResponse(
                    id = 2L,
                    guestId = null,
                    userId = 123L,
                    adminId = null,
                    productId = 1L,
                    status = "REQUESTED",
                    createdAt = now,
                    lastMessage = "상품 구매 문의드립니다",
                    lastMessageAt = now
                )

                every { chatService.createChatRoom(request) } returns response

                documentation(
                    identifier = "채팅방_생성_회원_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.POST, "/chat/rooms")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("senderType", "발신자 타입", "USER")
                        field("senderId", "발신자 ID", "123")
                        field("productId", "상품 ID", 1L)
                        field("initialMessage", "초기 메시지", "상품 구매 문의드립니다")
                    }

                    responseBody {
                        field("data.id", "채팅방 ID", 2)
                        optionalField("data.guestId", "게스트 ID (게스트인 경우)", null)
                        field("data.userId", "유저 ID", 123)
                        optionalField("data.adminId", "관리자 ID (배정된 경우)", null)
                        field("data.productId", "상품 ID", 1)
                        field("data.status", "채팅방 상태", "REQUESTED")
                        field("data.createdAt", "생성 시간", now.format(dateFormatter))
                        field("data.lastMessage", "마지막 메시지", "상품 구매 문의드립니다")
                        field("data.lastMessageAt", "마지막 메시지 시간", now.format(dateFormatter))
                        optionalField("error", "에러 정보", null)
                    }
                }
            }
        }

        describe("GET /chat/rooms - 채팅방 목록 조회") {
            val summary = "사용자의 채팅방 목록을 조회한다."

            it("회원의 채팅방 목록을 조회할 수 있다") {
                val now = LocalDateTime.now()
                val chatRooms = listOf(
                    ChatRoomResponse(
                        id = 1L,
                        guestId = null,
                        userId = 123L,
                        adminId = 1L,
                        productId = 1L,
                        status = "ON_CHAT",
                        createdAt = now.minusDays(1),
                        lastMessage = "네, 재고 있습니다",
                        lastMessageAt = now.minusHours(2)
                    )
                )

                every { chatService.getChatRoomList(userId = 123L, guestId = null) } returns chatRooms

                documentation(
                    identifier = "채팅방_목록_조회_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.GET, "/chat/rooms")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    queryParameters {
                        optionalField("userId", "유저 ID", 123)
                        optionalField("guestId", "게스트 ID", null)
                    }

                    responseBody {
                        // 첫 번째 채팅방
                        field("data[0].id", "채팅방 ID", 1)
                        optionalField("data[0].guestId", "게스트 ID", null)
                        field("data[0].userId", "유저 ID", 123)
                        optionalField("data[0].adminId", "관리자 ID", 1)
                        field("data[0].productId", "상품 ID", 1)
                        field("data[0].status", "채팅방 상태", "ON_CHAT")
                        field("data[0].createdAt", "생성 시간", chatRooms[0].createdAt.format(dateFormatter))
                        field("data[0].lastMessage", "마지막 메시지", "네, 재고 있습니다")
                        field("data[0].lastMessageAt", "마지막 메시지 시간", chatRooms[0].lastMessageAt?.format(dateFormatter))

                        optionalField("error", "에러 정보", null)
                    }
                }
            }
        }

        describe("GET /chat/rooms/{roomId} - 채팅방 상세 조회") {
            val summary = "특정 채팅방의 상세 정보를 조회한다."

            it("채팅방 상세 정보를 조회할 수 있다") {
                val roomId = 1L
                val now = LocalDateTime.now()
                val response = ChatRoomResponse(
                    id = roomId,
                    guestId = "guest-123",
                    userId = null,
                    adminId = 1L,
                    productId = 1L,
                    status = "ON_CHAT",
                    createdAt = now.minusDays(1),
                    lastMessage = "감사합니다",
                    lastMessageAt = now.minusMinutes(30)
                )

                every { chatService.getChatRoomDetail(roomId) } returns response

                documentation(
                    identifier = "채팅방_상세_조회_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = false,
                ) {
                    requestLine(HttpMethod.GET, "/chat/rooms/{roomId}") {
                        pathVariable("roomId", "채팅방 ID", roomId)
                    }

                    responseBody {
                        field("data.id", "채팅방 ID", 1)
                        field("data.guestId", "게스트 ID", "guest-123")
                        optionalField("data.userId", "유저 ID", null)
                        field("data.adminId", "관리자 ID", 1)
                        field("data.productId", "상품 ID", 1)
                        field("data.status", "채팅방 상태", "ON_CHAT")
                        field("data.createdAt", "생성 시간", response.createdAt.format(dateFormatter))
                        field("data.lastMessage", "마지막 메시지", "감사합니다")
                        field("data.lastMessageAt", "마지막 메시지 시간", response.lastMessageAt?.format(dateFormatter))
                        optionalField("error", "에러 정보", null)
                    }
                }
            }
        }

        describe("GET /chat/rooms/{roomId}/messages - 채팅 메시지 조회") {
            val summary = "채팅방의 메시지 목록을 페이징하여 조회한다."

            it("채팅 메시지 목록을 조회할 수 있다") {
                val roomId = 1L
                val resultRoomId = 1
                val now = LocalDateTime.now()
                val messages = listOf(
                    ChatMessageResponse(
                        id = 1L,
                        chatRoomId = roomId,
                        content = "안녕하세요",
                        senderType = SenderType.GUEST,
                        senderId = "guest-123",
                        senderName = "Guest",
                        createdAt = now.minusMinutes(10),
                        productInfo = null
                    )
                )

                val pageable = PageRequest.of(0, 20)
                val page = PageImpl(messages, pageable, messages.size.toLong())

                every { chatService.getChatMessages(eq(roomId), any()) } returns page

                documentation(
                    identifier = "채팅_메시지_조회_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = false,
                ) {
                    requestLine(HttpMethod.GET, "/chat/rooms/{roomId}/messages") {
                        pathVariable("roomId", "채팅방 ID", roomId)
                    }

                    queryParameters {
                        optionalField("page", "페이지 번호 (0부터 시작)", 0)
                        optionalField("size", "페이지 크기", 20)
                        optionalField("sort", "정렬 기준", "createdAt,desc")
                    }

                    responseBody {
                        // 첫 번째 메시지
                        field("data.content[0].id", "메시지 ID", 1)
                        field("data.content[0].chatRoomId", "채팅방 ID", resultRoomId)
                        field("data.content[0].content", "메시지 내용", "안녕하세요")
                        field("data.content[0].senderType", "발신자 타입", "GUEST")
                        field("data.content[0].senderId", "발신자 ID", "guest-123")
                        field("data.content[0].senderName", "발신자 이름", "Guest")
                        field("data.content[0].createdAt", "전송 시간", messages[0].createdAt.format(dateFormatter))
                        optionalField("data.content[0].productInfo", "상품 정보", null)

                        // 페이지 정보
                        field("data.totalElements", "전체 메시지 수", 1)
                        field("data.totalPages", "전체 페이지 수", 1)
                        field("data.number", "현재 페이지 번호", 0)
                        field("data.size", "페이지 크기", 20)
                        field("data.first", "첫 페이지 여부", true)
                        field("data.last", "마지막 페이지 여부", true)
                        field("data.numberOfElements", "현재 메세지 요소 수", 1)
                        field("data.empty", "빈 페이지 여부", false)

                        ignoredField("data.pageable")
                        ignoredField("data.sort")
                        optionalField("error", "에러 정보", null)
                    }
                }
            }
        }

        describe("PATCH /chat/rooms/{roomId}/status - 채팅방 상태 변경") {
            val summary = "채팅방의 상태를 변경한다. (관리자 권한 필요)"

            it("채팅방 상태를 변경할 수 있다") {
                val roomId = 1L
                val resultRoomId = 1
                val newStatus = "END"
                val now = LocalDateTime.now()

                val response = ChatRoomResponse(
                    id = roomId,
                    guestId = "guest-123",
                    userId = null,
                    adminId = 1L,
                    productId = 1L,
                    status = newStatus,
                    createdAt = now.minusDays(1),
                    lastMessage = "상담이 종료되었습니다",
                    lastMessageAt = now
                )

                every { chatService.updateChatRoomStatus(roomId, newStatus) } returns response
                every { chatMessageService.endChat(roomId) } just Runs

                documentation(
                    identifier = "채팅방_상태_변경_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.PATCH, "/chat/rooms/{roomId}/status") {
                        pathVariable("roomId", "채팅방 ID", roomId)
                    }

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer admin-token")
                    }

                    requestBody {
                        field("status", "변경할 상태 (REQUESTED, ON_CHAT, AWAITING, END)", newStatus)
                    }

                    responseBody {
                        field("data.id", "채팅방 ID", resultRoomId)
                        field("data.guestId", "게스트 ID", "guest-123")
                        optionalField("data.userId", "유저 ID", null)
                        field("data.adminId", "관리자 ID", 1)
                        field("data.productId", "상품 ID", 1)
                        field("data.status", "채팅방 상태", newStatus)
                        field("data.createdAt", "생성 시간", response.createdAt.format(dateFormatter))
                        field("data.lastMessage", "마지막 메시지", "상담이 종료되었습니다")
                        field("data.lastMessageAt", "마지막 메시지 시간", response.lastMessageAt?.format(dateFormatter))
                        optionalField("error", "에러 정보", null)
                    }
                }
            }
        }

        describe("POST /chat/rooms/{roomId}/admin-join - 관리자 채팅방 입장") {
            val summary = "관리자가 채팅방에 입장한다."

            it("관리자가 채팅방에 입장할 수 있다") {
                val roomId = 1L
                val adminId = 100L

                every { chatMessageService.handleAdminJoin(roomId, adminId) } just Runs

                documentation(
                    identifier = "관리자_채팅방_입장_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.POST, "/chat/rooms/{roomId}/admin-join") {
                        pathVariable("roomId", "채팅방 ID", roomId)
                    }

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer admin-token")
                    }

                    requestBody {
                        field("adminId", "관리자 ID", adminId)
                    }

                    responseBody {
                        field("data.message", "응답 메시지", "관리자가 채팅방에 입장했습니다.")
                        optionalField("error", "에러 정보", null)
                    }
                }
            }
        }
    }
}
