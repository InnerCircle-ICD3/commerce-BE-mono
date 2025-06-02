package com.fastcampus.commerce.file.interfaces

import com.fastcampus.commerce.common.error.CommonErrorCode
import com.fastcampus.commerce.file.application.FileService
import com.fastcampus.commerce.file.application.response.GeneratePresignedUrlResponse
import com.fastcampus.commerce.file.interfaces.request.GeneratePresignedUrlApiRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.called
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.verify
import org.mockito.ArgumentMatchers.isNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.UUID

@WebMvcTest(FileController::class)
class FileControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @MockkBean private val fileService: FileService,
) : DescribeSpec(
        {
            beforeEach {
                clearMocks(fileService)
            }

            describe("POST /files/presigned-url") {
                context("요청이 유효하면") {
                    it("presigned URL 응답을 반환한다") {
                        val request = GeneratePresignedUrlApiRequest(
                            fileName = "coffee.jpg",
                            contentType = "image/jpeg",
                            fileSize = 1024,
                            domainType = "product",
                            domainContext = "thumbnail",
                            contextId = null,
                        )
                        val uploadUrl = "https://s3.com/product/uuid/thumbnail-uuid.jpg"
                        val key = "product/uuid/thumbnail-uuid.jpg"
                        val contextId = UUID.randomUUID()
                        val serviceRequest = request.toServiceRequest()
                        every {
                            fileService.generatePresignedUrl(any<Long>(), serviceRequest)
                        } returns GeneratePresignedUrlResponse(
                            uploadUrl = uploadUrl,
                            key = key,
                            contextId = contextId,
                        )

                        mockMvc.post("/files/presigned-url") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(request)
                        }.andExpect {
                            status { isOk() }
                            jsonPath("$.data.uploadUrl") { value(uploadUrl) }
                            jsonPath("$.data.key") { value(key) }
                            jsonPath("$.data.contextId") { value(contextId.toString()) }
                            jsonPath("$.error") { isNull() }
                        }.andDo { print() }

                        verify(exactly = 1) { fileService.generatePresignedUrl(any<Long>(), serviceRequest) }
                    }
                }

                context("요청이 유효하지 않으면") {
                    it("파일명이 비어있으면 ") {
                        val request = GeneratePresignedUrlApiRequest(
                            fileName = "",
                            contentType = "image/jpeg",
                            fileSize = 1024,
                            domainType = "product",
                            domainContext = "thumbnail",
                            contextId = null,
                        )

                        mockMvc.post("/files/presigned-url") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(request)
                        }.andExpect {
                            status { isOk() }
                            jsonPath("$.data") { isNull() }
                            jsonPath("$.error.code") { value(CommonErrorCode.FIELD_ERROR.code) }
                            jsonPath("$.error.message") { isNotEmpty() }
                        }.andDo { print() }

                        verify { fileService wasNot called }
                    }
                }
            }
        },
    )
