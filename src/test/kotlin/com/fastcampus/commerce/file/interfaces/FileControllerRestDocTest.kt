package com.fastcampus.commerce.file.interfaces

import com.fastcampus.commerce.file.domain.error.FileErrorCode
import com.fastcampus.commerce.restdoc.documentation
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.test.web.servlet.MockMvc

@ExtendWith(RestDocumentationExtension::class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest
class FileControllerRestDocTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
    ) : DescribeSpec() {
        override fun extensions() = listOf(SpringExtension)

        init {
            beforeSpec {
                RestAssuredMockMvc.mockMvc(mockMvc)
            }

            val tag = "Admin-File"
            val privateResource = true

            describe("POST /files/presigned-url - Presigned URL 발급 요청") {
                val summary = "관리자가 업로드를 위한 Presigned URL을 발급한다."
                val description = """
                FIE-001: 파일명이 비어있습니다.
                FIE-002: 파일크기가 너무 큽니다.
                FIE-003: 허용되지 않는 파일타입입니다.
                """.trimMargin()

                it("파일 업로드를 위한 Presigned URL을 발급할 수 있다.") {
                    documentation(
                        identifier = "Presigned URL 발급 성공",
                        tag = tag,
                        summary = summary,
                        description = description,
                        privateResource = privateResource,
                    ) {
                        requestLine(HttpMethod.POST, "/files/presigned-url")

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                        }

                        requestBody {
                            field("fileName", "업로드할 파일의 원본 이름", "스타벅스캡슐.png")
                            field("contentType", "업로드할 파일의 MIME 타입", "image/png")
                            field("fileSize", "업로드할 파일의 크기(Byte)", 324893)
                            field("domainType", "업로드할 도메인(S3 prefix)", "product")
                            field("domainContext", "도메인 내 파일 유형 구분 (예: thumbnail, detail, profile 등)", "thumbnail")
                            optionalField("contextId", "업로드 세션을 식별하는 UUID (파일 그룹 식별자)", "f1df3364-1b59-4d76-bf69-618caffb4123")
                        }

                        responseBody {
                            field(
                                "data.uploadUrl",
                                "S3에 파일을 업로드하기 위한 Presigned URL",
                                "https://801base-example-docs.s3.amazonaws.com/product/f1df3364-1b59-4d76-bf69-618caffb4123/thumbnail.jpg",
                            )
                            field(
                                "data.key",
                                "업로드된 파일의 S3 저장 경로 (S3 object key)",
                                "product/f1df3364-1b59-4d76-bf69-618caffb4123/thumbnail.jpg",
                            )
                            field("data.contextId", "업로드 세션을 식별하는 UUID (파일 그룹 식별자)", "f1df3364-1b59-4d76-bf69-618caffb4123")
                            ignoredField("error")
                        }
                    }
                }
                it("허용되지 않은 Content-Type으로 Presigned URL 요청하면 예외가 발생한다.") {
                    documentation(
                        identifier = "Presigned URL 발급 실패",
                        tag = tag,
                        summary = summary,
                        description = description,
                        privateResource = privateResource,
                    ) {
                        requestLine(HttpMethod.POST, "/files/presigned-url")

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                        }

                        requestBody {
                            field("fileName", "업로드할 파일의 원본 이름", "바이러스.exe")
                            field("contentType", "허용되지 않은 MIME 타입", "application/x-msdownload")
                            field("fileSize", "업로드할 파일의 크기(Byte)", 1024)
                            field("domainType", "업로드할 도메인(S3 prefix)", "product")
                            field("domainContext", "도메인 내 파일 유형 구분", "thumbnail")
                            optionalField("contextId", "업로드 세션 UUID", "f1df3364-1b59-4d76-bf69-618caffb4123")
                        }

                        responseBody {
                            ignoredField("data")
                            field("error.code", "에러 코드", FileErrorCode.INVALID_FILE_POLICY.code)
                            field("error.message", "에러 메시지", FileErrorCode.INVALID_FILE_POLICY.message)
                        }
                    }
                }
            }
        }
    }
