package com.softline.dossier.be.graphql.schema.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.Client;
import com.softline.dossier.be.graphql.types.input.ClientInput;
import com.softline.dossier.be.service.ClientService;
import com.softline.dossier.be.service.exceptions.ClientReadableException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ClientSchemaResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    private final ClientService service;

    public Client createClient(ClientInput clientInput) {
        return service.create(clientInput);
    }

    public Client updateClient(ClientInput clientInput) {
        return service.update(clientInput);
    }

    public boolean deleteClient(Long id) throws ClientReadableException {
        return service.delete(id);
    }

    public List<Client> getAllClient() {
        return service.getAll();
    }

    public Client getClient(Long id) {
        return Database.findOrThrow(Client.class, id);
    }

    public List<Client> getClientsTable(String search) {
        return service.getClientsTable(search);
    }
}
