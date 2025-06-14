package com.fastcampus.commerce.admin.product.application

import com.fastcampus.commerce.admin.product.application.request.RegisterProductRequest
import com.fastcampus.commerce.admin.product.application.request.SearchAdminProductRequest
import com.fastcampus.commerce.admin.product.application.request.UpdateProductRequest
import com.fastcampus.commerce.admin.product.application.response.AdminProductDetailResponse
import com.fastcampus.commerce.admin.product.application.response.SearchAdminProductResponse
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.application.FileCommandService
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import com.fastcampus.commerce.file.domain.service.UploadedFileVerifier
import com.fastcampus.commerce.product.application.ProductCommandService
import com.fastcampus.commerce.product.application.ProductQueryService
import com.fastcampus.commerce.product.domain.entity.SellingStatus
import com.fastcampus.commerce.product.domain.model.ProductCategoryInfo
import com.fastcampus.commerce.product.domain.model.ProductInfo
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate

class AdminProductServiceTest : DescribeSpec(
    {
        val uploadedFileVerifier = mockk<UploadedFileVerifier>()
        val productCommandService = mockk<ProductCommandService>()
        val productQueryService = mockk<ProductQueryService>()
        val fileCommandService = mockk<FileCommandService>()
        val transactionTemplate = mockk<TransactionTemplate>()
        val service = AdminProductService(
            uploadedFileVerifier = uploadedFileVerifier,
            productCommandService = productCommandService,
            productQueryService = productQueryService,
            fileCommandService = fileCommandService,
            transactionTemplate = transactionTemplate,
        )

        beforeTest {
            clearMocks(
                uploadedFileVerifier,
                productCommandService,
                fileCommandService,
                transactionTemplate,
            )
        }

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

        describe("상품 등록") {
            val registerId = 1L
            val request = RegisterProductRequest(
                name = "상품A",
                price = 10000,
                quantity = 10,
                thumbnail = "https://example-bucket.amazonaws.com/thumbnail.jpg",
                detailImage = "https://example-bucket.amazonaws.com/detailImage.jpg",
                intensityId = 1L,
                cupSizeId = 10L,
            )

            it("상품을 등록할 수 있다.") {
                val expectedProductId = 1L
                every { uploadedFileVerifier.verifyFileWithS3Urls(any()) } returns Unit
                every { productCommandService.register(any()) } returns expectedProductId
                every { fileCommandService.markFilesAsSuccess(any()) } returns Unit
                every { transactionTemplate.execute(any<TransactionCallback<Long>>()) } answers {
                    val callback = it.invocation.args[0] as TransactionCallback<Long>
                    val txStatus = mockk<TransactionStatus>(relaxed = true)
                    callback.doInTransaction(txStatus)
                }

                val result = service.register(registerId, request)

                result shouldBe expectedProductId
                verify(exactly = 1) { uploadedFileVerifier.verifyFileWithS3Urls(request.files) }
                verify(exactly = 1) { productCommandService.register(request.toCommand(registerId)) }
                verify(exactly = 1) { fileCommandService.markFilesAsSuccess(request.files) }
            }
            context("상품 등록 실패") {
                it("업로드된 파일이 유효하지 않으면 예외가 발생한다.") {
                    every { uploadedFileVerifier.verifyFileWithS3Urls(any()) } throws CoreException(FileErrorCode.FILE_NOT_MATCH)

                    shouldThrow<CoreException> {
                        service.register(registerId, request)
                    }.errorCode shouldBe FileErrorCode.FILE_NOT_MATCH

                    verify(exactly = 1) { uploadedFileVerifier.verifyFileWithS3Urls(request.files) }
                    verify(exactly = 0) { productCommandService.register(request.toCommand(registerId)) }
                    verify(exactly = 0) { fileCommandService.markFilesAsSuccess(request.files) }
                }
            }
        }

        describe("상품 수정") {
            val updaterId = 1L
            val productId = 1L
            val request = UpdateProductRequest(
                id = productId,
                name = "상품A",
                price = 10000,
                quantity = 10,
                thumbnail = "https://example-bucket.amazonaws.com/thumbnail.jpg",
                detailImage = "https://example-bucket.amazonaws.com/detailImage.jpg",
                intensityId = 1L,
                cupSizeId = 10L,
                status = SellingStatus.UNAVAILABLE,
            )

            it("상품을 수정할 수 있다.") {
                every { uploadedFileVerifier.verifyFileWithS3Urls(any()) } returns Unit
                every { productCommandService.updateProduct(any()) } returns Unit
                every { productCommandService.updateInventory(any()) } returns Unit
                every { fileCommandService.markFilesAsSuccess(any()) } returns Unit
                every { transactionTemplate.execute(any<TransactionCallback<Unit>>()) } answers {
                    val callback = it.invocation.args[0] as TransactionCallback<Unit>
                    callback.doInTransaction(mockk(relaxed = true))
                }

                service.update(updaterId, request)

                verify(exactly = 1) { uploadedFileVerifier.verifyFileWithS3Urls(request.files) }
                verify(exactly = 1) { productCommandService.updateProduct(request.toCommand(updaterId)) }
                verify(exactly = 1) { productCommandService.updateInventory(request.toCommand(updaterId)) }
                verify(exactly = 1) { fileCommandService.markFilesAsSuccess(request.files) }
            }

            context("상품 수정 실패") {
                it("업로드된 파일이 유효하지 않으면 예외가 발생한다.") {
                    every { uploadedFileVerifier.verifyFileWithS3Urls(any()) } throws CoreException(FileErrorCode.FILE_NOT_MATCH)

                    shouldThrow<CoreException> {
                        service.update(updaterId, request)
                    }.errorCode shouldBe FileErrorCode.FILE_NOT_MATCH

                    verify(exactly = 1) { uploadedFileVerifier.verifyFileWithS3Urls(request.files) }
                    verify(exactly = 0) { productCommandService.updateProduct(request.toCommand(updaterId)) }
                    verify(exactly = 0) { productCommandService.updateInventory(request.toCommand(updaterId)) }
                    verify(exactly = 0) { fileCommandService.markFilesAsSuccess(request.files) }
                }
            }
        }

        describe("상품 삭제") {
            val deleterId = 1L
            val productId = 1L

            it("상품을 삭제할 수 있다.") {
                every { productCommandService.deleteProduct(productId) } returns Unit
                every { transactionTemplate.execute(any<TransactionCallback<Unit>>()) } answers {
                    val callback = it.invocation.args[0] as TransactionCallback<Unit>
                    callback.doInTransaction(mockk(relaxed = true))
                }

                service.delete(deleterId, productId)

                verify(exactly = 1) { productCommandService.deleteProduct(productId) }
            }
        }

        describe("상품 검색") {
            val request = SearchAdminProductRequest(
                name = "커피",
                intensityId = 1L,
                cupSizeId = 10L,
                status = SellingStatus.ON_SALE,
            )
            val pageable = mockk<Pageable>()

            it("상품을 검색할 수 있다.") {
                val productInfo = ProductInfo(
                    id = 1L,
                    name = "커피A",
                    price = 10000,
                    quantity = 100,
                    thumbnail = "https://example.com/thumbnail.jpg",
                    detailImage = "https://example.com/detailImage.jpg",
                    status = SellingStatus.ON_SALE,
                )
                val productCategoryInfo = ProductCategoryInfo(
                    intensity = "강함",
                    cupSize = "L",
                )
                val expectedResponse = SearchAdminProductResponse.of(productInfo, productCategoryInfo)
                val expectedPage = mockk<Page<SearchAdminProductResponse>> {
                    every { content } returns listOf(expectedResponse)
                    every { totalElements } returns 1L
                    every { totalPages } returns 1
                    every { number } returns 0
                    every { size } returns 10
                }

                every { productQueryService.searchProductsForAdmin(request, pageable) } returns expectedPage

                val result = service.searchProducts(request, pageable)

                result shouldBe expectedPage
                result.content shouldHaveSize 1
                result.content[0] shouldBe expectedResponse
                verify(exactly = 1) { productQueryService.searchProductsForAdmin(request, pageable) }
            }

            it("검색 조건이 없어도 모든 상품을 검색할 수 있다.") {
                val emptyRequest = SearchAdminProductRequest()
                val productInfo1 = ProductInfo(
                    id = 1L,
                    name = "커피A",
                    price = 10000,
                    quantity = 100,
                    thumbnail = "https://example.com/thumbnail.jpg",
                    detailImage = "https://example.com/detailImage.jpg",
                    status = SellingStatus.ON_SALE,
                )
                val productCategoryInfo1 = ProductCategoryInfo(
                    intensity = "강함",
                    cupSize = "L",
                )
                val expectedResponse1 = SearchAdminProductResponse.of(productInfo1, productCategoryInfo1)
                val expectedPage = mockk<Page<SearchAdminProductResponse>> {
                    every { content } returns listOf(expectedResponse1)
                    every { totalElements } returns 2L
                    every { totalPages } returns 1
                    every { number } returns 0
                    every { size } returns 10
                }

                every { productQueryService.searchProductsForAdmin(emptyRequest, pageable) } returns expectedPage

                val result = service.searchProducts(emptyRequest, pageable)

                result shouldBe expectedPage
                result.content shouldHaveSize 1
                verify(exactly = 1) { productQueryService.searchProductsForAdmin(emptyRequest, pageable) }
            }

            it("검색 결과가 없으면 빈 페이지를 반환한다.") {
                val noResultRequest = SearchAdminProductRequest(
                    name = "존재하지않는상품",
                )
                val emptyPage = mockk<Page<SearchAdminProductResponse>> {
                    every { content } returns emptyList()
                    every { totalElements } returns 0L
                    every { totalPages } returns 0
                    every { number } returns 0
                    every { size } returns 10
                    every { isEmpty } returns true
                }

                every { productQueryService.searchProductsForAdmin(noResultRequest, pageable) } returns emptyPage

                val result = service.searchProducts(noResultRequest, pageable)

                result shouldBe emptyPage
                result.content shouldHaveSize 0
                result.isEmpty shouldBe true
                verify(exactly = 1) { productQueryService.searchProductsForAdmin(noResultRequest, pageable) }
            }

            it("이름으로만 검색할 수 있다.") {
                val nameOnlyRequest = SearchAdminProductRequest(name = "커피")
                val productInfo = ProductInfo(
                    id = 1L,
                    name = "커피A",
                    price = 10000,
                    quantity = 100,
                    thumbnail = "https://example.com/thumbnail.jpg",
                    detailImage = "https://example.com/detailImage.jpg",
                    status = SellingStatus.ON_SALE,
                )
                val productCategoryInfo = ProductCategoryInfo(
                    intensity = "강함",
                    cupSize = "L",
                )
                val expectedResponse = SearchAdminProductResponse.of(productInfo, productCategoryInfo)
                val expectedPage = mockk<Page<SearchAdminProductResponse>> {
                    every { content } returns listOf(expectedResponse)
                    every { totalElements } returns 1L
                    every { totalPages } returns 1
                    every { number } returns 0
                    every { size } returns 10
                }

                every { productQueryService.searchProductsForAdmin(nameOnlyRequest, pageable) } returns expectedPage

                val result = service.searchProducts(nameOnlyRequest, pageable)

                result shouldBe expectedPage
                verify(exactly = 1) { productQueryService.searchProductsForAdmin(nameOnlyRequest, pageable) }
            }

            it("상태로만 검색할 수 있다.") {
                val statusOnlyRequest = SearchAdminProductRequest(status = SellingStatus.UNAVAILABLE)
                val productInfo = ProductInfo(
                    id = 2L,
                    name = "커피B",
                    price = 15000,
                    quantity = 100,
                    thumbnail = "https://example.com/thumbnail.jpg",
                    detailImage = "https://example.com/detailImage.jpg",
                    status = SellingStatus.UNAVAILABLE,
                )
                val productCategoryInfo = ProductCategoryInfo(
                    intensity = "중간",
                    cupSize = "M",
                )
                val expectedResponse = SearchAdminProductResponse.of(productInfo, productCategoryInfo)
                val expectedPage = mockk<Page<SearchAdminProductResponse>> {
                    every { content } returns listOf(expectedResponse)
                    every { totalElements } returns 1L
                    every { totalPages } returns 1
                    every { number } returns 0
                    every { size } returns 10
                }

                every { productQueryService.searchProductsForAdmin(statusOnlyRequest, pageable) } returns expectedPage

                val result = service.searchProducts(statusOnlyRequest, pageable)

                result shouldBe expectedPage
                verify(exactly = 1) { productQueryService.searchProductsForAdmin(statusOnlyRequest, pageable) }
            }
        }

        describe("상품 상세 조회") {
            val productId = 1L

            it("상품 상세 정보를 조회할 수 있다.") {
                val productInfo = ProductInfo(
                    id = productId,
                    name = "커피A",
                    price = 10000,
                    quantity = 100,
                    thumbnail = "https://example.com/thumbnail.jpg",
                    detailImage = "https://example.com/detailImage.jpg",
                    status = SellingStatus.ON_SALE,
                )
                val productCategoryInfo = ProductCategoryInfo(
                    intensity = "강함",
                    cupSize = "L",
                )
                val expectedResponse = AdminProductDetailResponse.of(productInfo, productCategoryInfo)

                every { productQueryService.getProductDetailForAdmin(productId) } returns expectedResponse

                val result = service.getProduct(productId)

                result shouldBe expectedResponse
                result.id shouldBe productId
                result.name shouldBe "커피A"
                result.price shouldBe 10000
                result.quantity shouldBe 100
                result.thumbnail shouldBe "https://example.com/thumbnail.jpg"
                result.detailImage shouldBe "https://example.com/detailImage.jpg"
                result.intensity shouldBe "강함"
                result.cupSize shouldBe "L"
                result.status shouldBe SellingStatus.ON_SALE
                verify(exactly = 1) { productQueryService.getProductDetailForAdmin(productId) }
            }

            it("상품 상세 정보에 모든 필드가 포함되어 있다.") {
                val productInfo = ProductInfo(
                    id = 3L,
                    name = "프리미엄 커피",
                    price = 25000,
                    quantity = 50,
                    thumbnail = "https://example.com/premium-thumbnail.jpg",
                    detailImage = "https://example.com/premium-detail.jpg",
                    status = SellingStatus.ON_SALE,
                )
                val productCategoryInfo = ProductCategoryInfo(
                    intensity = "매우 강함",
                    cupSize = "XL",
                )
                val expectedResponse = AdminProductDetailResponse.of(productInfo, productCategoryInfo)

                every { productQueryService.getProductDetailForAdmin(3L) } returns expectedResponse

                val result = service.getProduct(3L)

                result shouldBe expectedResponse
                // 모든 필드가 null이 아닌지 확인
                result.id shouldBe 3L
                result.name shouldBe "프리미엄 커피"
                result.price shouldBe 25000
                result.quantity shouldBe 50
                result.thumbnail shouldBe "https://example.com/premium-thumbnail.jpg"
                result.detailImage shouldBe "https://example.com/premium-detail.jpg"
                result.intensity shouldBe "매우 강함"
                result.cupSize shouldBe "XL"
                result.status shouldBe SellingStatus.ON_SALE
                verify(exactly = 1) { productQueryService.getProductDetailForAdmin(3L) }
            }
        }
    },
)
