package com.fastcampus.commerce.file.domain.service

import com.fastcampus.commerce.file.domain.model.ActualFile

interface UploadFileReader {
    fun read(key: String): ActualFile
}
