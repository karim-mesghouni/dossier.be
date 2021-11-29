package com.softline.dossier.be.graphql.directivies;

//
//public class CanDirective implements SchemaDirectiveWiring {
//
//    @Override
//    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
//        GraphQLFieldDefinition field = environment.getElement();
//        String action = (String) environment.getDirective().getArgument("action").getValue();
//
//        GraphQLFieldsContainer parentType = environment.getFieldsContainer();
//        //
//        // build a data fetcher that first checks authorisation roles before then calling the original data fetcher
//        //
//        var original = environment.getCodeRegistry().getDataFetcher(parentType, field);
//        DataFetcherFactories.wrapDataFetcher(field.getDataFetcher()
//        DataFetcher<?> authDataFetcher = dataFetchingEnvironment -> {
//            if (AttributeBasedAccessControlEvaluator.can(action,null)) {
//                return original.get(dataFetchingEnvironment);
//            } else {
//                return null;
//            }
//        };
//        //
//        // now change the field definition to have the new authorising data fetcher
//        environment.getCodeRegistry().dataFetcher(parentType, field, authDataFetcher);
//        return field.transform(builder -> builder.dataFetcher(authDataFetcher));
//    }
//}