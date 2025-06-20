package com.fastcampus.commerce.common.config

import com.p6spy.engine.spy.P6SpyOptions
import com.p6spy.engine.spy.appender.MessageFormattingStrategy
import org.hibernate.engine.jdbc.internal.FormatStyle
import org.springframework.context.annotation.Configuration
import jakarta.annotation.PostConstruct

@Configuration
class P6SpyConfig {
    @PostConstruct
    fun configureP6Spy() {
        P6SpyOptions.getActiveInstance().logMessageFormat = P6SpyFormatter::class.java.name
    }
}

class P6SpyFormatter : MessageFormattingStrategy {
    override fun formatMessage(
        connectionId: Int,
        now: String?,
        elapsed: Long,
        category: String?,
        prepared: String?,
        sql: String?,
        url: String?,
    ): String {
        return buildString {
            appendLine()
            append("🔍 ")

            // 카테고리별 이모지와 색상
            when (category?.lowercase()) {
                "statement" -> append("📝 SQL")
                "commit" -> append("✅ COMMIT")
                "rollback" -> append("❌ ROLLBACK")
                "batch" -> append("📦 BATCH")
                else -> append("💾 $category")
            }

            // 실행시간 표시 (성능에 따라 이모지 변경)
            append(" | ")
            when {
                elapsed > 1000 -> append("🐌 ${elapsed}ms")
                elapsed > 500 -> append("⚠️ ${elapsed}ms")
                elapsed > 100 -> append("⏱️ ${elapsed}ms")
                else -> append("⚡ ${elapsed}ms")
            }

            // 커넥션 정보
            append(" | Connection-$connectionId")
            appendLine()

            // SQL 포매팅
            sql?.let { sqlString ->
                if (sqlString.isNotBlank()) {
                    append(formatSql(category, sqlString))
                }
            }

            appendLine()
            append("=".repeat(80))
        }
    }

    private fun formatSql(category: String?, sql: String): String {
        if (sql.isBlank()) return sql

        return try {
            val trimmedSql = sql.trim()

            // Statement 카테고리일 때만 포매팅 적용
            if (category?.equals("statement", true) == true) {
                when {
                    trimmedSql.lowercase().startsWith("select") -> {
                        FormatStyle.BASIC.formatter.format(trimmedSql)
                    }
                    trimmedSql.lowercase().startsWith("insert") -> {
                        formatInsert(trimmedSql)
                    }
                    trimmedSql.lowercase().startsWith("update") -> {
                        formatUpdate(trimmedSql)
                    }
                    trimmedSql.lowercase().startsWith("delete") -> {
                        formatDelete(trimmedSql)
                    }
                    else -> {
                        FormatStyle.BASIC.formatter.format(trimmedSql)
                    }
                }
            } else {
                trimmedSql
            }
        } catch (e: Exception) {
            sql
        }
    }

    private fun formatInsert(sql: String): String {
        return sql.replace("insert into", "INSERT INTO")
            .replace("values", "\nVALUES")
            .replace("(", "(\n    ")
            .replace(")", "\n)")
            .replace(",", ",\n    ")
    }

    private fun formatUpdate(sql: String): String {
        return sql.replace("update", "UPDATE")
            .replace("set", "\nSET")
            .replace("where", "\nWHERE")
            .replace(",", ",\n    ")
    }

    private fun formatDelete(sql: String): String {
        return sql.replace("delete from", "DELETE FROM")
            .replace("where", "\nWHERE")
    }
}
