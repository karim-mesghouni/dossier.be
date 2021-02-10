package com.softline.dossier.be.gql;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.List;

public class GQLExpetion extends RuntimeException implements GraphQLError {


    public  GQLExpetion(String messge){
        super(messge);
    }
    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorClassification getErrorType() {
        return null;
    }
}
