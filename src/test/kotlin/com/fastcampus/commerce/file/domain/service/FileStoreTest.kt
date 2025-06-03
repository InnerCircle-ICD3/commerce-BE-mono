package com.fastcampus.commerce.file.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.application.request.GeneratePresignedUrlRequest
import com.fastcampus.commerce.file.domain.entity.FileMetadata
import com.fastcampus.commerce.file.domain.entity.StorageContextKey
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import com.fastcampus.commerce.file.domain.model.DomainType
import com.fastcampus.commerce.file.domain.model.FilePath
import com.fastcampus.commerce.file.domain.model.FileStatus
import com.fastcampus.commerce.file.domain.model.FileType
import com.fastcampus.commerce.file.domain.model.UploadUrl
import com.fastcampus.commerce.file.domain.repository.FileMetadataRepository
import com.fastcampus.commerce.file.domain.repository.StorageContextKeyRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class FileStoreTest : FunSpec(
    {
        val fileMetadataRepository = mockk<FileMetadataRepository>()
        val storageContextKeyRepository = mockk<StorageContextKeyRepository>()
        val filePathGenerator = mockk<FilePathGenerator>()
        val fileReader = mockk<FileReader>()
        val fileStore = FileStore(
            storageContextKeyRepository,
            fileMetadataRepository,
            filePathGenerator,
            fileReader,
        )

        val uploaderId = 1L
        val contextKey = StorageContextKey(
            adminId = uploaderId,
        )
        val contextKeyId = contextKey.id

        val request = GeneratePresignedUrlRequest(
            fileName = "coffee.jpg",
            contentType = "image/jpeg",
            fileSize = 1,
            domainType = DomainType.PRODUCT,
            domainContext = "thumbnail",
            fileType = FileType.IMAGE,
            contextId = null,
        )

        val filePath = FilePath(
            fileName = "thumbnail-uuid.jpg",
            path = "product/$contextKeyId/thumbnail-uuid.jpg",
        )

        beforeTest {
            clearMocks(fileReader, fileMetadataRepository, storageContextKeyRepository, filePathGenerator)
        }

        context("prepareGeneratePresignedUrl") {
            test("요청에 contextId가 null이면 새 StorageContextKey를 만들고 FileMetadata를 저장한다.") {
                every { storageContextKeyRepository.save(any()) } returns contextKey
                every { filePathGenerator.generate(any(), any()) } returns filePath
                every { fileMetadataRepository.save(any()) } answers { firstArg() }

                val result = fileStore.prepareGeneratePresignedUrl(uploaderId, request)

                result.contextKey shouldBe contextKeyId
                result.storedPath shouldBe filePath.path
                result.storedFileName shouldBe filePath.fileName
                result.originalFileName shouldBe request.fileName
                result.contentType shouldBe request.contentType
                result.fileSize shouldBe request.fileSize

                verify(exactly = 1) { storageContextKeyRepository.save(any()) }
                verify(exactly = 0) { fileReader.getStorageContextKey(any(), any()) }
            }

            context("요청에 contextId가 있고,") {
                test("uploaderId가 일치하면 기존 StorageContextKey를 재사용해서 FileMetadata를 저장한다.") {
                    val requestWithContextId = request.copy(contextId = contextKeyId.toString())

                    every { fileReader.getStorageContextKey(contextKeyId.toString(), uploaderId) } returns contextKey
                    every { filePathGenerator.generate(any(), any()) } returns filePath
                    every { fileMetadataRepository.save(any()) } answers { firstArg() }

                    val result = fileStore.prepareGeneratePresignedUrl(uploaderId, requestWithContextId)

                    result.contextKey shouldBe contextKeyId
                    verify(exactly = 1) { fileReader.getStorageContextKey(contextKeyId.toString(), uploaderId) }
                    verify(exactly = 0) { storageContextKeyRepository.save(any()) }
                }

                test("uploaderId가 다르면 INVALID_STORAGE_CONTEXT_KEY 예외가 발생한다.") {
                    val wrongUploaderId = Long.MIN_VALUE
                    val requestWithContextId = request.copy(contextId = contextKeyId.toString())

                    every {
                        fileReader.getStorageContextKey(contextKeyId.toString(), wrongUploaderId)
                    } throws CoreException(FileErrorCode.INVALID_STORAGE_CONTEXT_KEY)

                    shouldThrow<CoreException> {
                        fileStore.prepareGeneratePresignedUrl(wrongUploaderId, requestWithContextId)
                    }.errorCode shouldBe FileErrorCode.INVALID_STORAGE_CONTEXT_KEY
                }
            }
        }
        context("markFilesAsSuccess") {
            test("파일 메타데이터의 상태를 SUCCESS로 변경할 수 있다.") {
                val storedPath = "product/uuid/test.png"
                val metadata = FileMetadata(
                    contextKey = mockk(),
                    storedPath = storedPath,
                    storedFileName = "test.png",
                    originalFileName = "test.png",
                    contentType = "image/png",
                    fileSize = 1024,
                    status = FileStatus.PENDING,
                )
                every { fileReader.getFileMetadataByStoredPath(storedPath) } returns metadata
                val uploadUrl = UploadUrl("https://test-bucket.amazon.com/$storedPath")

                fileStore.markFilesAsSuccess(listOf(uploadUrl))

                metadata.status shouldBe FileStatus.SUCCESS
            }

            test("존재하지 않는 파일 메타데이터의 상태를 SUCCESS로 변경하려고하면 METADATA_NOW_FOUND 예외가 발생한다.") {
                val storedPath = "product/uuid/test.png"
                every { fileReader.getFileMetadataByStoredPath(storedPath) } throws CoreException(FileErrorCode.METADATA_NOW_FOUND)
                val uploadUrl = UploadUrl("https://test-bucket.amazon.com/$storedPath")

                shouldThrow<CoreException> {
                    fileStore.markFilesAsSuccess(listOf(uploadUrl))
                }.errorCode shouldBe FileErrorCode.METADATA_NOW_FOUND
            }
        }
    },
)
