package com.gujo.uminity.config;

import com.gujo.uminity.config.interceptor.ViewCountInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ViewCountInterceptor viewCountInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(viewCountInterceptor)
                .addPathPatterns("/api/posts/*");  // /api/posts/{postId}
    }
}
