package com.fastcampus.commerce.cart.interfaces

import com.fastcampus.commerce.cart.application.CartItemService
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
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@WebMvcTest(CartItemController::class)
class CartItemControllerTest : DescribeSpec(){
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var cartItemService: CartItemService

    val tag = "Cart"
    val privateResource = true

    init{
        beforeSpec{
            RestAssuredMockMvc.mockMvc(mockMvc)
        }

        describe("POST /carts/items - 장바구니에 상품 등록") {
            val summary = "사용자의 장바구니에 상품을 추가할 수 있다."
            it("사용자의 장바구니에 상품을 추가할 수 있다."){
                val request = CartCreateRequest(
                    productId = 1L,
                    quantity = 100
                )

                val response = CartCreateResponse(
                    quantity = 80,
                    stockQuantity = 80,
                    requiresQuantityAdjustment = true
                )

                every { cartItemService.addToCart(any<Long>(),request.productId,request.quantity) } returns response

                documentation(
                    identifier = "장바구니에_상품_추가_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ){
                    requestLine(HttpMethod.POST, "/cart/items")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("productId", "상품Id",request.productId)
                        field("quantity","상품수량",request.quantity)
                    }

                    responseBody {
                        field("data.quantity","상품 수량",request.quantity)
                        field("data.stockQuantity", "재고 수량", 80)
                        field("data.requiresQuantityAdjustment","상품 수량이 재고 수량을 초과함",true)
                        ignoredField("error")
                    }
                }
            }
    }
}
}
