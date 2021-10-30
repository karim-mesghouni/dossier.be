package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.Client;
import com.softline.dossier.be.graphql.types.input.ClientInput;
import com.softline.dossier.be.repository.ClientRepository;
import com.softline.dossier.be.service.ClientService;
import com.softline.dossier.be.service.exceptions.ClientReadableException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")

public class ClientSchemaResolver extends SchemaResolverBase<Client, ClientInput, ClientRepository, ClientService> {


    public Client createClient(ClientInput clientInput) throws IOException, ClientReadableException {
        return create(clientInput);
    }

    public Client updateClient(ClientInput clientInput) throws ClientReadableException {
        return update(clientInput);
    }

    public boolean deleteClient(Long id) throws ClientReadableException {
        return delete(id);
    }

    public List<Client> getAllClient() {
        return getAll();
    }

    public Client getClient(Long id) {
        return get(id);
    }

    public List<Client> getClientsTable(String search) {
        return service.getClientsTable(search);
    }

}
