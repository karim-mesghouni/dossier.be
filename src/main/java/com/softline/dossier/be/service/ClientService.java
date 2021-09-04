package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.Client;
import com.softline.dossier.be.graphql.types.input.ClientInput;
import com.softline.dossier.be.repository.ClientRepository;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional

@Service
public class ClientService extends IServiceBase<Client, ClientInput, ClientRepository>
{
    @Override
    public List<Client> getAll() {
        return  repository.findAll();
    }

    @Override
    public Client create(ClientInput input) {
        return repository.save(Client.builder().name(input.getName()).address(input.getAddress()).build());
    }

    @Override
    public Client update(ClientInput input) {
        Client client = repository.getOne(input.getId());
        client.setAddress(input.getAddress());
        client.setName(input.getName());
        return repository.save(client);
    }

    @SneakyThrows
    @Override
    public boolean delete(long id) {
        Client client = repository.findWithFilesById(id);
        if(client.getFiles().size() > 0)
        {
            throw new Exception("client has open files");
        }
        repository.deleteById(id);
        return true;
    }

    @Override
    public Client getById(long id) {
        return null;
    }

    public List<Client> getClientsTable(String search)
    {
        return repository.findAllWithContactsByNameContaining(search);
    }
}
