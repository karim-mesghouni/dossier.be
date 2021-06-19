package com.softline.dossier.be.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry
                .addResourceHandler("/images/**")
                .addResourceLocations(new File("C:\\Users\\PC\\Documents\\fileStorage").toURI().toString());
        registry
                .addResourceHandler("/attached/**")
                .addResourceLocations(new File("C:\\Users\\PC\\Documents\\fileStorage2").toURI().toString());

    }

}
