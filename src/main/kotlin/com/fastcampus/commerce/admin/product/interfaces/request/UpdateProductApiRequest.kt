package com.fastcampus.commerce.admin.product.interfaces.request

import com.fastcampus.commerce.admin.product.application.request.UpdateProductRequest
import com.fastcampus.commerce.product.domain.entity.SellingStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero

data class UpdateProductApiRequest(
    @field:NotBlank(message = "파일명이 비어있습니다.")
    val name: String? = null,
    @field:NotNull(message = "가격을 입력해주세요.")
    @field:Positive(message = "가격은 양수여야합니다.")
    val price: Int? = null,
    @field:NotNull(message = "재고를 입력해주세요.")
    @field:PositiveOrZero(message = "재고는 음수일 수 없습니다.")
    val quantity: Int? = null,
    @field:NotBlank(message = "썸네일이 비어있습니다.")
    val thumbnail: String? = null,
    @field:NotBlank(message = "상품 상세이미지가 비어있습니다.")
    val detailImage: String? = null,
    @field:NotNull(message = "강도 카테고리 아이디가 비어있습니다.")
    val intensityId: Long? = null,
    @field:NotNull(message = "컵사이즈 카테고리 아이디가 비어있습니다.")
    val cupSizeId: Long? = null,
    @field:NotNull(message = "판매 상태 값이 비어있습니다.")
    val status: SellingStatus? = null,
) {
    fun toServiceRequest(productId: Long): UpdateProductRequest =
        UpdateProductRequest(
            id = productId,
            name = name!!,
            price = price!!,
            quantity = quantity!!,
            detailImage = detailImage!!,
            thumbnail = thumbnail!!,
            intensityId = intensityId!!,
            cupSizeId = cupSizeId!!,
            status = status!!,
        )
}
