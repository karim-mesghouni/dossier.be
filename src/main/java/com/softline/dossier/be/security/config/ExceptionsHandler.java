package com.softline.dossier.be.security.config;

import com.oembedler.moon.graphql.boot.error.ThrowableGraphQLError;
import com.softline.dossier.be.service.exceptions.ClientReadableException;
import graphql.GraphQLError;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;

@Component
public class ExceptionsHandler {

    /**
     * this exception contains a written message by us,
     * we will send it to the client to give a feedback message.
     */
    @ExceptionHandler(ClientReadableException.class)
    public GraphQLError exceptionHandler(ClientReadableException e) {
        return new ThrowableGraphQLError(e, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public GraphQLError exceptionHandler(AccessDeniedException e) {
        return new ThrowableGraphQLError(e, "Erreur de privilège");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public GraphQLError exceptionHandler(EntityNotFoundException e) {
        return new ThrowableGraphQLError(e, "Entity not found");
    }
}
