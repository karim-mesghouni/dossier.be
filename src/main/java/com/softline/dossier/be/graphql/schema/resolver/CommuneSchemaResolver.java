package com.softline.dossier.be.graphql.schema.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.Commune;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@PreAuthorize("isAuthenticated()")
public class CommuneSchemaResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    protected List<Commune> getAllCommune() {
        return Database.findAll(Commune.class);
    }
}
