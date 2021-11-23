package com.softline.dossier.be.config;

import com.coxautodev.graphql.tools.SchemaParserDictionary;
import com.softline.dossier.be.graphql.types.FileDTO;
import com.softline.dossier.be.security.config.SecurityAuditorAware;
import com.softline.dossier.be.security.domain.Agent;
import graphql.schema.GraphQLScalarType;
import graphql.servlet.core.ApolloScalars;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.servlet.Filter;
import java.util.Optional;

/**
 * All the custom beans are registered here
 * we can use @AutoWire annotation to invoke these methods with the corresponding method return type
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class Beans {

//    @Value("${pool.size:4}")
//    private int poolSize;;
//
//    @Value("${queue.capacity:300}")
//    private int queueCapacity;
//
//    @Bean(name="workExecutor")
//    public TaskExecutor taskExecutor() {
//        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
//        taskExecutor.setMaxPoolSize(poolSize);
//        taskExecutor.setQueueCapacity(queueCapacity);
//        taskExecutor.afterPropertiesSet();
//        return taskExecutor;
//    }

    @Bean
    public ModelMapper modelMapper() {
        var mm = new ModelMapper();
//        mm.getConfiguration().setAmbiguityIgnored(true);
        mm.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return mm;
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
        scheduler.setPoolSize(30);
        scheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        return scheduler;
    }
}
