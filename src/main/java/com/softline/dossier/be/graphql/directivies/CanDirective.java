package com.softline.dossier.be.graphql.directivies;


import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.security.config.AttributeBasedAccessControlEvaluator;
import graphql.Scalars;
import graphql.schema.*;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.modelmapper.ModelMapper;

import static com.softline.dossier.be.Application.getBean;

public class CanDirective implements SchemaDirectiveWiring {

    @Override
    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
        GraphQLFieldDefinition field = environment.getElement();
        GraphQLFieldsContainer parentType = environment.getFieldsContainer();
        String action = (String) environment.getDirective().getArgument("action").getValue();
        String entity = (String) environment.getDirective().getArgument("entity").getValue();

        DataFetcher originalFetcher = environment.getCodeRegistry().getDataFetcher(parentType, field);
        DataFetcher dataFetcher = dataFetchingEnvironment -> {
            var in = getBean(ModelMapper.class).map(dataFetchingEnvironment.getArguments().values().stream().findFirst().orElseThrow(), Database.getEntityType(entity).getJavaType());
            var out = Database.findOrThrow(Database.getEntityType(entity).getJavaType(), ((HasId) in).getId());
            AttributeBasedAccessControlEvaluator.DenyOrProceed(action, out);
            return out;
        };
        //
        // now change the field definition to have the new authorising data fetcher
        FieldCoordinates coordinates = FieldCoordinates.coordinates(parentType, field);
        environment.getCodeRegistry().dataFetcher(coordinates, dataFetcher);
        return field.transform(builder -> builder
                .argument(GraphQLArgument
                        .newArgument()
                        .name("action")
                        .type(Scalars.GraphQLString)
                ).argument(GraphQLArgument
                        .newArgument()
                        .name("entity")
                        .type(Scalars.GraphQLString))
        );
    }
}