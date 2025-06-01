package com.fastcampus.commerce.admin.product.application

import com.fastcampus.commerce.product.domain.entity.SellingStatus
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class AdminProductServiceTest : DescribeSpec(
    {
        val service = AdminProductService()

        describe("상품 판매상태 목록 조회") {
            it("상품 판매 목록을 조회할 수 있다.") {
                val result = service.getSellingStatus()

                result shouldHaveSize SellingStatus.entries.size

                result.forEach { data ->
                    val matchedEnum = SellingStatus.valueOf(data.code)
                    data.label shouldBe matchedEnum.label
                }
            }
        }
    },
)
