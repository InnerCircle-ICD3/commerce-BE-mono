package com.fastcampus.commerce.file.infrastructure.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Configuration
class S3Config(
    @Value("\${cloud.aws.region.static}") val region: String,
) {
    @Bean
    fun s3Presigner(): S3Presigner {
        return S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.builder().build())
            .build()
    }

    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.builder().build())
            .build()
    }
}
