package com.prodify.cqrs.ProductService.command.api.config;

import com.prodify.cqrs.ProductService.command.api.interceptor.RequestIdInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RequestIdInterceptor requestIdInterceptor;

    @Autowired
    public WebMvcConfig(RequestIdInterceptor requestIdInterceptor) {
        this.requestIdInterceptor = requestIdInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestIdInterceptor);
    }
}
