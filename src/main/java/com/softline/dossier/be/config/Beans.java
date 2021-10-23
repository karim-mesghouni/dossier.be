package com.softline.dossier.be.config;

import com.coxautodev.graphql.tools.SchemaParserDictionary;
import com.softline.dossier.be.graphql.types.FileDTO;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class Beans
{

    @Bean
    public ModelMapper modelMapper()
    {
        return new ModelMapper();
    }

    @Bean
    public SchemaParserDictionary schemaParserDictionary()
    {
        return new SchemaParserDictionary()
                .add(FileDTO.class);
    }

    @Bean
    public AuditorAware<String> auditorProvider()
    {
        return () -> Optional.ofNullable("chathuranga");
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
