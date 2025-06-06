package com.fastcampus.commerce.cart.interfaces

import com.fastcampus.commerce.cart.application.CartItemService
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
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@WebMvcTest(CartItemController::class)
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

        describe("POST /carts/items - 장바구니에 상품 등록") {
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
                    requestLine(HttpMethod.POST, "/cart/items"){
                    }


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

        describe("PATCH /cart/items - 장바구니 내의 상품 수량 변경") {
            val summary = "장바구니 내 상품의 수량을 변경할 수 있다."
            it("장바구니 내 상품의 수량을 변경할 수 있다.") {
                val userId = 1L
                val productId = 1L
                var finalQuantity = 1
                val cartId = 1L
                val request = CartUpdateRequest(
                    productId = productId,
                    quantity = 100,
                    userId = userId,
                    cartId = cartId,
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

                every { cartItemService.updateCartItem(request) } returns response

                documentation(
                    identifier = "장바구니_상품_수정_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.PATCH, "/cart/items")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("cartId", "카트 아이디", request.cartId)
                        field("userId", "유저 아이디", request.userId)
                        field("productId", "상품 아이디", request.productId)
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
    }
}
