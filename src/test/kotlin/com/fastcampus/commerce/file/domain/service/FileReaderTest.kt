package com.fastcampus.commerce.file.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.domain.entity.FileMetadata
import com.fastcampus.commerce.file.domain.entity.StorageContextKey
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import com.fastcampus.commerce.file.domain.repository.FileMetadataRepository
import com.fastcampus.commerce.file.domain.repository.StorageContextKeyRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import java.util.Optional

class FileReaderTest : FunSpec(
    {

        val storageContextKeyRepository = mockk<StorageContextKeyRepository>()
        val fileMetadataRepository = mockk<FileMetadataRepository>()
        val fileReader = FileReader(
            storageContextKeyRepository = storageContextKeyRepository,
            fileMetadataRepository = fileMetadataRepository,
        )

        beforeTest {
            clearMocks(
                storageContextKeyRepository,
                fileMetadataRepository,
            )
        }

        context("getStorageContextKey") {
            val uploaderId = 1L
            val contextKey = StorageContextKey(adminId = uploaderId)
            val contextKeyId = contextKey.id.toString()

            test("uploaderId가 일치하면 contextKey를 반환한다") {
                every {
                    storageContextKeyRepository.findByIdAndAdminId(contextKey.id, uploaderId)
                } returns Optional.of(contextKey)

                val result = fileReader.getStorageContextKey(contextKeyId, uploaderId)

                result shouldBe contextKey
            }

            test("존재하지 않거나 uploaderId가 다르면 INVALID_STORAGE_CONTEXT_KEY 예외를 던진다") {
                every {
                    storageContextKeyRepository.findByIdAndAdminId(contextKey.id, uploaderId)
                } returns Optional.empty()

                shouldThrow<CoreException> {
                    fileReader.getStorageContextKey(contextKeyId, uploaderId)
                }.errorCode shouldBe FileErrorCode.INVALID_STORAGE_CONTEXT_KEY
            }
        }

        context("getFileMetadataByStoredPath") {
            val storedPath = "product/uuid/test.png"
            test("StoredPath로 파일 메타데이터를 조회할 수 있다.") {
                val fileMetadata = mockk<FileMetadata>()
                every { fileMetadataRepository.findByStoredPath(storedPath) } returns Optional.of(fileMetadata)

                val result = fileReader.getFileMetadataByStoredPath(storedPath)

                result shouldBe fileMetadata
            }
            test("존재하지 않는 StoredPath로 파일 메타데이터를 조회하면 METADATA_NOW_FOUND 예외가 발생한다.") {
                every { fileMetadataRepository.findByStoredPath(storedPath) } returns Optional.empty()

                shouldThrow<CoreException> {
                    fileReader.getFileMetadataByStoredPath(storedPath)
                }.errorCode shouldBe FileErrorCode.METADATA_NOT_FOUND
            }
        }
    },
)
