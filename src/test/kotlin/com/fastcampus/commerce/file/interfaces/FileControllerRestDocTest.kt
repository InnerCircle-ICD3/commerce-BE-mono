package com.fastcampus.commerce.file.interfaces

import com.fastcampus.commerce.admin.product.application.AdminProductService
import com.fastcampus.commerce.admin.product.interfaces.AdminProductController
import com.fastcampus.commerce.admin.product.interfaces.request.RegisterProductApiRequest
import com.fastcampus.commerce.file.application.FileCommandService
import com.fastcampus.commerce.file.application.response.GeneratePresignedUrlResponse
import com.fastcampus.commerce.file.interfaces.request.GeneratePresignedUrlApiRequest
import com.fastcampus.commerce.restdoc.documentation
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc
import java.util.UUID

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@WebMvcTest(FileController::class)
class FileControllerRestDocTest: DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var fileCommandService: FileCommandService

    val tag = "Admin-File"
    val privateResource = true

    init {
        beforeSpec {
            RestAssuredMockMvc.mockMvc(mockMvc)
        }

        describe("POST /files/presigned-url - Presigned URL 발급") {
            val summary = "Presigned URL을 발급받을 수 있다."

            it("Presigned URL을 발급받을 수 있다.") {
                val request = GeneratePresignedUrlApiRequest(
                    fileName = "사진입니다.jpg",
                    contentType = "image/jpeg",
                    fileSize = 1024 * 1024,
                    domainType = "PRODUCT",
                    domainContext = "thumbnail",
                    contextId = "6d851850-3b6f-4230-97fe-1a55b44fc862",
                )

                val response =  GeneratePresignedUrlResponse(
                    uploadUrl = "https://test-801base-bucket.com/product/6d851850-3b6f-4230-97fe-1a55b44fc862/thumbnail-uuid.jpg",
                    key = "product/6d851850-3b6f-4230-97fe-1a55b44fc862/thumbnail-uuid.jpg",
                    contextId = UUID.fromString("6d851850-3b6f-4230-97fe-1a55b44fc862"),
                )

                every { fileCommandService.generatePresignedUrl(any<Long>(), request.toServiceRequest()) } returns response

                documentation(
                    identifier = "PresignedURL_발급_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.POST, "/files/presigned-url")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("fileName", "상품명", request.fileName)
                        field("contentType", "가격", request.contentType)
                        field("fileSize", "재고 수량", request.fileSize)
                        field("domainType", "썸네일 이미지", request.domainType)
                        field("domainContext", "상세 이미지", request.domainContext)
                        optionalField("contextId", "업로드 세션 ID", request.contextId)
                    }

                    responseBody {
                        field("data.uploadUrl", "업로드 경로", response.uploadUrl)
                        field("data.key", "S3 Object Key", response.key)
                        field("data.contextId", "업로드 세션 ID", response.contextId.toString())
                        ignoredField("error")
                    }
                }
            }
        }
    }
}
