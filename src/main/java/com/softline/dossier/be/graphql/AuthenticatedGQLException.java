package com.softline.dossier.be.graphql;

import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import org.springframework.security.core.AuthenticationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthenticatedGQLException extends AuthenticationException implements GraphQLError {


    public AuthenticatedGQLException(String messge) {
        super(messge);
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorClassification getErrorType() {
        return ErrorType.ValidationError;
    }


    @Override
    public Map<String, Object> getExtensions() {
        var extensions = new HashMap();
        extensions.put("code", 401);
        return extensions;
    }
}
