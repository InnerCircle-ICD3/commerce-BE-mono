package com.fastcampus.commerce.admin.product.application

import com.fastcampus.commerce.admin.product.application.request.RegisterProductRequest
import com.fastcampus.commerce.admin.product.application.request.UpdateProductRequest
import com.fastcampus.commerce.admin.product.application.response.SellingStatusResponse
import com.fastcampus.commerce.common.error.CommonErrorCode
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.application.FileCommandService
import com.fastcampus.commerce.file.domain.service.UploadedFileVerifier
import com.fastcampus.commerce.product.application.ProductCommandService
import com.fastcampus.commerce.product.domain.entity.SellingStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate

@Service
class AdminProductService(
    private val uploadedFileVerifier: UploadedFileVerifier,
    private val productCommandService: ProductCommandService,
    private val fileCommandService: FileCommandService,
    private val transactionTemplate: TransactionTemplate,
) {
    fun getSellingStatus(): List<SellingStatusResponse> {
        return SellingStatus.entries
            .map(SellingStatusResponse::from)
    }

    fun register(adminId: Long, request: RegisterProductRequest): Long {
        uploadedFileVerifier.verifyFileWithS3Urls(request.files)
        val command = request.toCommand(adminId)
        return transactionTemplate.execute {
            val register = productCommandService.register(command)
            fileCommandService.markFilesAsSuccess(request.files)
            register
        } ?: throw CoreException(CommonErrorCode.SERVER_ERROR)
    }

    fun update(adminId: Long, request: UpdateProductRequest) {
        // TODO:
        //  이미지가 바뀌지 않은 경우 불필요한 요청. 시간이 없어 그대로 진행.
        //  이미지만 변경하는 PATCH를 만들던가,
        //  product를 조회한 후 url이 변경되었는지 확인 후 요청하도록 수정
        //  현재 markFilesAsSuccess는 멱등성있게 작성되어있어 문제 없음.
        uploadedFileVerifier.verifyFileWithS3Urls(request.files)
        val command = request.toCommand(adminId)
        transactionTemplate.execute {
            productCommandService.updateProduct(command)
            fileCommandService.markFilesAsSuccess(request.files)
            productCommandService.updateInventory(command)
        }
    }
}
