package com.softline.dossier.be;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Responsible for serving files in the storage directory
 */
@Configuration
public class StorageServer implements WebMvcConfigurer {
    @Value("${filesystem.storage.absolute-path}")
    String storagePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler(
                        "/assets/**",
                        "/attachments/**"
//                        "/resources/**"
                )
                .addResourceLocations(
                        "file:" + Paths.get(storagePath, "/assets/") + "\\",
                        "file:" + Paths.get(storagePath, "/attachments/" + "\\")
//                        "classpath:/"
                );
    }
}