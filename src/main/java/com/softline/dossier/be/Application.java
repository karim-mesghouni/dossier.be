package com.softline.dossier.be;

import com.softline.dossier.be.security.config.SecurityAuditorAware;
import com.softline.dossier.be.security.domain.Agent;
import graphql.schema.GraphQLScalarType;
import graphql.servlet.core.ApolloScalars;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

import javax.servlet.Filter;

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing(auditorAwareRef = "auditorAware")

public class Application
{

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Filter OpenFilter()
    {
        return new OpenEntityManagerInViewFilter();
    }

    @Bean
    public GraphQLScalarType uploadScalar()
    {
        return ApolloScalars.Upload;
    }

    @Bean
    public AuditorAware<Agent> auditorAware()
    {
        return new SecurityAuditorAware();
    }
}
