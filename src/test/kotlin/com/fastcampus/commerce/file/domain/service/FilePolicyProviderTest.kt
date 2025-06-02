package com.fastcampus.commerce.file.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import com.fastcampus.commerce.file.domain.model.DomainType
import com.fastcampus.commerce.file.domain.model.FileType
import com.fastcampus.commerce.file.infrastructure.config.FilePolicyProperties
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class FilePolicyProviderTest : FunSpec(
    {
        val props = FilePolicyProperties().apply {
            default = mapOf(
                FileType.IMAGE to FilePolicyProperties.Policy(1, setOf("jpg", "jpeg")),
            )
            override = listOf(
                FilePolicyProperties.OverridePolicy(
                    domain = DomainType.PRODUCT,
                    context = "thumbnail",
                    fileTypes = mapOf(
                        FileType.IMAGE to FilePolicyProperties.Policy(5, setOf("png")),
                    ),
                ),
            )
        }

        val provider = FilePolicyProvider(props)

        test("override 정책이 있으면 override 정책을 반환한다.") {
            val result = provider.resolve(DomainType.PRODUCT, "thumbnail", FileType.IMAGE)
            result.maxSize shouldBe 5 * 1024 * 1024L
            result.allowedExtensions shouldContainExactlyInAnyOrder setOf("png")
        }

        test("override 정책이 없으면 default 정책을 반환한다.") {
            val result = provider.resolve(DomainType.PRODUCT, "detail", FileType.IMAGE)
            result.maxSize shouldBe 1 * 1024 * 1024L
            result.allowedExtensions shouldContainExactlyInAnyOrder setOf("jpg", "jpeg")
        }

        test("정책이 없으면 INVALID_FILE_TYPE 예외가 발생한다.") {
            shouldThrow<CoreException> {
                provider.resolve(DomainType.PRODUCT, "thumbnail", FileType.VIDEO)
            }.errorCode shouldBe FileErrorCode.INVALID_FILE_TYPE
        }
    },
)
