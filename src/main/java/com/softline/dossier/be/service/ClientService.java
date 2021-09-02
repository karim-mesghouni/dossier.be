package com.softline.dossier.be.service;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.domain.Client;
import com.softline.dossier.be.graphql.types.input.ClientInput;
import com.softline.dossier.be.repository.ClientRepository;
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
    public Client create(ClientInput clientInput) {
        return null;
    }

    @Override
    public Client update(ClientInput clientInput) {
        return null;
    }

    @Override
    public boolean delete(long id) {
        return false;
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
