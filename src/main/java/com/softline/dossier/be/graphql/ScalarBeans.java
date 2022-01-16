package com.softline.dossier.be.graphql;

import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import graphql.servlet.core.ApolloScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ScalarBeans {

    @Bean
    public GraphQLScalarType uploadScalar() {
        return ApolloScalars.Upload;
    }

    @Bean
    public GraphQLScalarType voidScalar() {
        return GraphQLScalarType.newScalar()
                .name("Void")
                .description("Void is always transformed into true boolean value")
                .coercing(new Coercing<>() {
                    @Override
                    public Object serialize(Object dataFetcherResult) {
                        return true;
                    }

                    @Override
                    public Object parseValue(Object input) {
                        return true;
                    }

                    @Override
                    public Object parseLiteral(Object input) {
                        return true;
                    }
                }).build();
    }

}
