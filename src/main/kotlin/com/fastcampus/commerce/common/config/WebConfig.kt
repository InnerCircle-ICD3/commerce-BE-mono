package com.fastcampus.commerce.common.config

import com.fastcampus.commerce.auth.interfaces.web.security.resolver.RoleBasedUserArgumentResolver
import com.fastcampus.commerce.common.resolver.CustomPageableArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val customPageableArgumentResolver: CustomPageableArgumentResolver,
    private val roleBasedUserArgumentResolver: RoleBasedUserArgumentResolver,
) : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/api/**")
            // .addResourceLocations("file:./docs/api/")
            .addResourceLocations("classpath:/static/swagger-ui/")
            .setCachePeriod(0)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(customPageableArgumentResolver)
        resolvers.add(roleBasedUserArgumentResolver)
    }
}
