package com.fastcampus.commerce.file.domain.model

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import java.util.Locale

enum class DomainType(
    val domainName: String,
) {
    PRODUCT("product"),
    ;

    companion object {
        fun from(value: String) =
            runCatching { valueOf(value.uppercase(Locale.getDefault())) }.getOrElse {
                throw CoreException(FileErrorCode.INVALID_DOMAIN_TYPE)
            }
    }
}
