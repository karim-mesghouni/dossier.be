package com.softline.dossier.be.config;

import com.coxautodev.graphql.tools.SchemaParserDictionary;
import com.softline.dossier.be.graphql.types.FileDTO;
import com.softline.dossier.be.security.config.SecurityAuditorAware;
import com.softline.dossier.be.security.domain.Agent;
import graphql.schema.GraphQLScalarType;
import graphql.servlet.core.ApolloScalars;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.servlet.Filter;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
/**
 * All the custom beans are registered here
 * we can use @AutoWire annotation to invoke these methods with the corresponding method return type
 */
public class Beans {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public SchemaParserDictionary schemaParserDictionary() {
        return new SchemaParserDictionary().add(FileDTO.class);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable("chathuranga");
    }

    @Bean
    public Filter OpenFilter() {
        return new OpenEntityManagerInViewFilter();
    }

    @Bean
    public GraphQLScalarType uploadScalar() {
        return ApolloScalars.Upload;
    }

    @Bean
    public AuditorAware<Agent> auditorAware() {
        return new SecurityAuditorAware();
    }


    @Bean
    public ThreadPoolTaskScheduler scheduler() {
        var scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        return scheduler;
    }
}
