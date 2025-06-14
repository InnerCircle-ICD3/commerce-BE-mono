package com.fastcampus.commerce.common.config

import com.fastcampus.commerce.common.resolver.CustomPageableArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val customPageableArgumentResolver: CustomPageableArgumentResolver,
) : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/swagger-ui/**")
            .addResourceLocations("classpath:/static/swagger-ui/")
            .setCachePeriod(0)
        
        // Spring Boot의 기본 static 리소스 핸들러도 추가
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(0)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver?>) {
        resolvers.add(customPageableArgumentResolver)
    }
}
