package com.softline.dossier.be.config;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {
    @SneakyThrows
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry
                .addResourceHandler("/images/**")
                .addResourceLocations(new ClassPathResource("fileStorage").getFile().toURI().toString());
        registry
                .addResourceHandler("/attached/**")
              .addResourceLocations(        new ClassPathResource("fileStorage2").getFile().toURI().toString());
//                .addResourceLocations(new File("C:\\Users\\PC\\Documents\\fileStorage2").toURI().toString());

    }

}
