package com.softline.dossier.be.security.config;

import com.oembedler.moon.graphql.boot.error.ThrowableGraphQLError;
import graphql.GraphQLError;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;

@Component
public class ExceptionsHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public GraphQLError exceptionHandler(AccessDeniedException e) {
        return new ThrowableGraphQLError(e, "Privilege error");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public GraphQLError exceptionHandler(EntityNotFoundException e) {
        return new ThrowableGraphQLError(e, "Entity not found");
    }
}
