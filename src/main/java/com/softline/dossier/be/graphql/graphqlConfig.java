package com.softline.dossier.be.graphql;

import com.oembedler.moon.graphql.boot.error.ThrowableGraphQLError;
import graphql.GraphQLError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;

@Component

public class graphqlConfig {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(AccessDeniedException.class)
    public GraphQLError exceptionHandler(AccessDeniedException e) {
        return new ThrowableGraphQLError(e, "Unauthorized request");
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public GraphQLError exceptionHandler(EntityNotFoundException e) {
        return new ThrowableGraphQLError(e, "Entity not found");
    }

}

