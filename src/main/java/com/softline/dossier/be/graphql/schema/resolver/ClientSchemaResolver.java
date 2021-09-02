package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.ActivityField;
import com.softline.dossier.be.domain.Client;
import com.softline.dossier.be.graphql.types.input.ActivityFieldInput;
import com.softline.dossier.be.graphql.types.input.ClientInput;
import com.softline.dossier.be.repository.ActivityFieldRepository;
import com.softline.dossier.be.repository.ClientRepository;
import com.softline.dossier.be.service.ActivityFieldService;
import com.softline.dossier.be.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")

public class ClientSchemaResolver extends SchemaResolverBase<Client, ClientInput, ClientRepository, ClientService> {


    public Client createClient(ClientInput clientInput) throws IOException {
        return create(clientInput);
    }
    public Client updateClient(ClientInput clientInput){
        return update(clientInput);
    }
    public boolean deleteClient(Long id){
        return delete(id);
    }
    public List<Client> getAllClient(){
        return getAll();
    }
    public Client getClient(Long id){
        return get(id);
    }

    public List<Client> getClientsTable(String search)
    {
        return service.getClientsTable(search);
    }

}
