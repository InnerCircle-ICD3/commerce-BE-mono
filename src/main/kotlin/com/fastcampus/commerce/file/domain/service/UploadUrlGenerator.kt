package com.fastcampus.commerce.file.domain.service

import com.fastcampus.commerce.file.domain.entity.FileMetadata

interface UploadUrlGenerator {
    fun generate(fileMetadata: FileMetadata): String
}
