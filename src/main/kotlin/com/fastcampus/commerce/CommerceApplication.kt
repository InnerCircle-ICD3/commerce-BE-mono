package com.fastcampus.commerce

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class CommerceApplication

fun main(args: Array<String>) {
    runApplication<CommerceApplication>(*args)
}
