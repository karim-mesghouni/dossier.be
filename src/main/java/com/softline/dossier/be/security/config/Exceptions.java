package com.softline.dossier.be.security.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.nio.file.AccessDeniedException;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class Exceptions extends WebMvcConfigurerAdapter {
    @Bean
    public SimpleMappingExceptionResolver exceptionResolver() {
        SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
        exceptionResolver.setExcludedExceptions(AccessDeniedException.class);
        return exceptionResolver;
    }
}