package com.fastcampus.commerce.cart.interfaces

import com.fastcampus.commerce.admin.product.interfaces.AdminProductControllerRestDocTest
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
    lateinit var productReader: ProductReader

    val tag = "Cart"
    val privateResource = true

    init {
        beforeSpec {
            RestAssuredMockMvc.mockMvc(mockMvc)
        }

        describe("PATCH /cart/item - 장바구니 내의 상품 수량 변경"){
            val summary = "장바구니 내 상품의 수량을 변경할 수 있다."
            it("장바구니 내 상품의 수량을 변경할 수 있다."){
                val userId = 1L
                val productId = 1L
                var finalQuantity = 1
                val request = CartUpdateRequest(
                    productId = productId,
                    quantity = 100,
                    userId = userId,
                )

                val inventory = Inventory(
                    productId = productId,
                    quantity = 80
                )

                every{productReader.getInventoryByProductId(productId)} returns inventory

                if(request.quantity > inventory.quantity){
                    finalQuantity = inventory.quantity
                } else{
                    finalQuantity = request.quantity
                }

                val response = CartUpdateResponse(
                    userId = userId,
                    productId = productId,
                    quantity = finalQuantity,
                    stockQuantity = inventory.quantity,
                    requiresQuantityAdjustment = true,
                )

                every { cartItemService.updateCartItem(userId,  request) } returns response

                documentation(
                    identifier = "상품_수정_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.PATCH, "/cart/items")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody{
                        field("userId","유저 아이디", request.userId)
                        field("productId","카트 아이디", request.productId)
                        field("quantity", "수량",request.quantity)
                    }

                    responseBody {
                        field("productId","카트 아이디",response.productId)
                        field("userId","유저 아이디",response.userId)
                        field("quantity","수량",response.quantity)
                        field("stockQuantity","재고 수량",response.stockQuantity)
                        field("requiresQuantityAdjustment","수량 변경 여부",response.requiresQuantityAdjustment)
                    }
                }
            }
        }
    }
}
