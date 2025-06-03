package com.fastcampus.commerce.admin.product.application

import com.fastcampus.commerce.admin.product.application.request.RegisterProductRequest
import com.fastcampus.commerce.admin.product.application.request.UpdateProductRequest
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.application.FileCommandService
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import com.fastcampus.commerce.file.domain.service.UploadedFileVerifier
import com.fastcampus.commerce.product.application.ProductCommandService
import com.fastcampus.commerce.product.domain.entity.SellingStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate

class AdminProductServiceTest : DescribeSpec(
    {
        val uploadedFileVerifier = mockk<UploadedFileVerifier>()
        val productCommandService = mockk<ProductCommandService>()
        val fileCommandService = mockk<FileCommandService>()
        val transactionTemplate = mockk<TransactionTemplate>()
        val service = AdminProductService(
            uploadedFileVerifier = uploadedFileVerifier,
            productCommandService = productCommandService,
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
    },
)
