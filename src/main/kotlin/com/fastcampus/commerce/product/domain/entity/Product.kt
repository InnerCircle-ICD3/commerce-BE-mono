package com.fastcampus.commerce.product.domain.entity

import com.fastcampus.commerce.common.entity.BaseEntity
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.product.domain.error.ProductErrorCode
import com.fastcampus.commerce.product.domain.model.ProductUpdater
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@SQLDelete(sql = "update products set is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted = false and status = 'ON_SALE'")
@Table(name = "products")
@Entity
class Product(
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var price: Int,
    @Column(nullable = false)
    var thumbnail: String,
    @Column(nullable = false)
    var detailImage: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: SellingStatus = SellingStatus.ON_SALE,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    var isDeleted: Boolean = false

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updatedAt: LocalDateTime

    @Column
    var deletedAt: LocalDateTime? = null

    init {
        validate()
    }

    fun validate() {
        validateName()
        validatePrice()
    }

    private fun validateName() {
        if (name.isBlank()) {
            throw CoreException(ProductErrorCode.PRODUCT_NAME_EMPTY)
        }
        if (name.length > MAX_NAME_LENGTH) {
            throw CoreException(ProductErrorCode.PRODUCT_NAME_TOO_LONG)
        }
    }

    private fun validatePrice() {
        if (price <= 0) {
            throw CoreException(ProductErrorCode.PRICE_NOT_POSITIVE)
        }
    }

    fun update(updater: ProductUpdater) {
        this.name = updater.name
        this.price = updater.price
        this.thumbnail = updater.thumbnail
        this.detailImage = updater.detailImage
        this.status = updater.status
        validate()
    }

    companion object {
        const val MAX_NAME_LENGTH = 255
    }
}
