package com.fastcampus.commerce.cart.interfaces

import com.fastcampus.commerce.cart.application.CartItemService
import com.fastcampus.commerce.config.TestConfig
import com.fastcampus.commerce.product.domain.entity.Inventory
import com.fastcampus.commerce.product.domain.service.ProductReader
import com.fastcampus.commerce.restdoc.documentation
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@WebMvcTest(CartItemController::class)
@Import(TestConfig::class)
class CartItemControllerRestDocTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var cartItemService: CartItemService

    @MockkBean
    lateinit var productReader: ProductReader

    val tag = "Cart"
    val privateResource = true

    init {
        beforeSpec {
            RestAssuredMockMvc.mockMvc(mockMvc)
        }

        describe("POST /cart-items - 장바구니에 상품 등록") {
            val summary = "사용자의 장바구니에 상품을 추가할 수 있다."
            it("사용자의 장바구니에 상품을 추가할 수 있다.") {
                val userId = 1L
                val request = CartCreateRequest(
                    productId = 1L,
                    quantity = 100,
                )

                val response = CartCreateResponse(
                    quantity = 80,
                    stockQuantity = 80,
                    requiresQuantityAdjustment = true,
                )

                every { cartItemService.addToCart(userId, request.productId, request.quantity) } returns response

                documentation(
                    identifier = "장바구니에_상품_추가_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.POST, "/cart-items")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("productId", "상품Id", request.productId)
                        field("quantity", "상품수량", request.quantity)
                    }

                    responseBody {
                        field("data.quantity", "상품 수량", response.quantity)
                        field("data.stockQuantity", "재고 수량", response.stockQuantity)
                        field("data.requiresQuantityAdjustment", "상품 수량이 재고 수량을 초과함", response.requiresQuantityAdjustment)
                        ignoredField("error")
                    }
                }
            }
        }

        describe("PATCH /cart-items/{cartItemId} - 장바구니 내의 상품 수량 변경") {
            val summary = "장바구니 내 상품의 수량을 변경할 수 있다."
            it("장바구니 내 상품의 수량을 변경할 수 있다.") {
                val userId = 1L
                val productId = 1L
                var finalQuantity = 1
                val cartItemId = 1L
                val request = CartUpdateRequest(
                    quantity = 100,
                )

                val inventory = Inventory(
                    productId = productId,
                    quantity = 80,
                )

                every { productReader.getInventoryByProductId(productId) } returns inventory

                if (request.quantity > inventory.quantity) {
                    finalQuantity = inventory.quantity
                } else {
                    finalQuantity = request.quantity
                }

                val response = CartUpdateResponse(
                    userId = userId,
                    productId = productId,
                    quantity = finalQuantity,
                    stockQuantity = inventory.quantity,
                    requiresQuantityAdjustment = true,
                )

                every { cartItemService.updateCartItem(userId, cartItemId, request) } returns response

                documentation(
                    identifier = "장바구니_상품_수정_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.PATCH, "/cart-items/{cartItemId}") {
                        pathVariable("cartItemId", "장바구니 상품 아이디", cartItemId)
                    }

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("cartItemId", "카트 아이디", cartItemId)
                        field("quantity", "수량", request.quantity)
                    }

                    responseBody {
                        field("data.productId", "상품 아이디", response.productId.toInt())
                        field("data.userId", "유저 아이디", response.userId.toInt())
                        field("data.quantity", "수량", response.quantity)
                        field("data.stockQuantity", "재고 수량", response.stockQuantity)
                        field("data.requiresQuantityAdjustment", "수량 변경 여부", response.requiresQuantityAdjustment)
                        ignoredField("error")
                    }
                }
            }
        }

        describe("GET /cart-items - 장바구니 상품 목록 조회") {
            val summary = "장바구니에 담긴 상품 목록을 조회할 수 있다."
            it("장바구니에 담긴 상품 목록을 조회할 수 있다.") {
                val cartItems = listOf(
                    CartItemRetrieve(
                        cartItemId = 101,
                        productId = 1,
                        productName = "Product 1",
                        quantity = 2,
                        price = 10000,
                        stockQuantity = 10,
                        thumbnail = "thumbnail1.jpg",
                        isAvailable = true,
                    ),
                    CartItemRetrieve(
                        cartItemId = 102,
                        productId = 2,
                        productName = "Product 2",
                        quantity = 3,
                        price = 20000,
                        stockQuantity = 5,
                        thumbnail = "thumbnail2.jpg",
                        isAvailable = true,
                    ),
                )

                val cartResponse = CartRetrievesResponse(
                    totalPrice = 80000,
                    deliveryPrice = 0,
                    cartItems = cartItems,
                )

                every { cartItemService.getCarts(1L) } returns cartResponse

                documentation(
                    identifier = "장바구니_상품_목록_조회_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.GET, "/cart-items")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    responseBody {
                        // 최상위 필드들
                        field("data.totalPrice", "총 상품 금액 (배송비 제외)", 80000)
                        field("data.deliveryPrice", "배송비", 0)
                        field(
                            "data.cartItems",
                            "장바구니 상품 목록",
                            listOf(
                                mapOf(
                                    "cartItemId" to 101,
                                    "productId" to 1,
                                    "productName" to "Product 1",
                                    "quantity" to 2,
                                    "price" to 10000,
                                    "stockQuantity" to 10,
                                    "thumbnail" to "thumbnail1.jpg",
                                    "isAvailable" to true,
                                ),
                                mapOf(
                                    "cartItemId" to 102,
                                    "productId" to 2,
                                    "productName" to "Product 2",
                                    "quantity" to 3,
                                    "price" to 20000,
                                    "stockQuantity" to 5,
                                    "thumbnail" to "thumbnail2.jpg",
                                    "isAvailable" to true,
                                ),
                            ),
                        )

                        // cartItems 배열 내부 필드들
                        field("data.cartItems[0].cartItemId", "장바구니 아이템 ID", 101)
                        field("data.cartItems[0].productId", "상품 ID", 1)
                        field("data.cartItems[0].productName", "상품명", "Product 1")
                        field("data.cartItems[0].quantity", "수량", 2)
                        field("data.cartItems[0].price", "상품 단가", 10000)
                        field("data.cartItems[0].stockQuantity", "재고 수량", 10)
                        field("data.cartItems[0].thumbnail", "상품 썸네일 이미지 URL", "thumbnail1.jpg")
                        field("data.cartItems[0].isAvailable", "구매 가능 여부", true)
                        ignoredField("error")
                    }
                }
            }
        }

        describe("DELETE /cart-items - 장바구니 상품 삭제") {
            val summary = "장바구니에 추가된 상품을 삭제할 수 있다."

            it("장바구니에 추가된 상품을 삭제할 수 있다.") {
                val ids = listOf(2L)
                every { cartItemService.deleteCartItems(ids) } returns ids.size

                documentation(
                    identifier = "장바구니_상품_삭제_성공",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.DELETE, "/cart-items")

                    queryParameters {
                        optionalField("cartItems", "상품 아이디", listOf(2))
                    }

                    responseBody {
                        field("data.message", "응답 메시지", "Successfully deleted ${ids.size} cart items")
                        ignoredField("error")
                    }
                }
            }
        }
    }
}
