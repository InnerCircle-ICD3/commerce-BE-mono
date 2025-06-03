package com.fastcampus.commerce.admin.product.application

import com.fastcampus.commerce.admin.product.application.request.RegisterProductRequest
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
}
