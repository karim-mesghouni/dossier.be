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
                .description("Void java return type(always treated as null)")
                .coercing(new Coercing<>() {
                    @Override
                    public Object serialize(Object dataFetcherResult) {
                        return null;
                    }

                    @Override
                    public Object parseValue(Object input) {
                        return null;
                    }

                    @Override
                    public Object parseLiteral(Object input) {
                        return null;
                    }
                }).build();
    }

}
