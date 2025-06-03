package com.fastcampus.commerce.file.domain.model

import java.net.URI

data class UploadUrl(
    val url: String,
) {
    val storedPath get() = URI(url).path.removePrefix("/")

    companion object {
        fun from(url: String): UploadUrl = UploadUrl(url)

        fun of(urls: List<String>): List<UploadUrl> = urls.map { from(it) }
    }
}
