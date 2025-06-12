package com.fastcampus.commerce.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val roleBasedUserArgumentResolver: RoleBasedUserArgumentResolver
) : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/api/**")
            .addResourceLocations("file:./docs/api/")
            .setCachePeriod(0)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(roleBasedUserArgumentResolver)
    }
}
