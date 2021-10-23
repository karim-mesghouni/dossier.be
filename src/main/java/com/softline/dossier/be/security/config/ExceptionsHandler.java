package com.softline.dossier.be.security.config;

import com.oembedler.moon.graphql.boot.error.ThrowableGraphQLError;
import com.softline.dossier.be.service.exceptions.ClientReadableException;
import graphql.GraphQLError;
import graphql.GraphQLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;

@Component
@Slf4j
public class ExceptionsHandler
{

    /**
     * this exception contains a written message by us,
     * we will send it to the client to give a feedback message.
     */
    @ExceptionHandler(ClientReadableException.class)
    public GraphQLError exceptionHandler(ClientReadableException e)
    {
        log.error("ClientReadableException: {}", e.getMessage());
        return new ThrowableGraphQLError(e, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public GraphQLException exceptionHandler(AccessDeniedException e)
    {
        log.error("AccessDeniedException");
        return new GraphQLException("Erreur de privilège");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public GraphQLError exceptionHandler(EntityNotFoundException e)
    {
        log.error("EntityNotFoundException");
        return new ThrowableGraphQLError(e, "Entité introuvable");
    }

    @ExceptionHandler(Throwable.class)
    public GraphQLError exceptionHandler(Throwable e)
    {
        log.error("Throwable: {}", e.getMessage());
        return new ThrowableGraphQLError(e, "server error");
    }
}
