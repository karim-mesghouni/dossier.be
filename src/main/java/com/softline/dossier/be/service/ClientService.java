package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.Client;
import com.softline.dossier.be.domain.Contact;
import com.softline.dossier.be.graphql.types.input.ClientInput;
import com.softline.dossier.be.repository.ClientRepository;
import com.softline.dossier.be.repository.ContactRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
@Transactional

@Service
public class ClientService extends IServiceBase<Client, ClientInput, ClientRepository>
{
    @Autowired
    ContactRepository contactRepository;

    @Override
    public List<Client> getAll() {
        return repository.findAll();
    }

    @Override
    @PreAuthorize("hasPermission(null, 'CREATE_CLIENT')")
    public Client create(ClientInput input) {
        Client client = Client.builder().name(input.getName()).address(input.getAddress()).build();
        List<Contact> contacts = new ArrayList<>();
        for (var contact: input.getContacts())
        {
            contacts.add(Contact.builder().name(contact.getName()).email(contact.getEmail()).phone(contact.getPhone()).client(client).build());
        }
        client.setContacts(contacts);
        return repository.save(client);
    }

    @Override
    public Client update(ClientInput input) {
        Client client = repository.getOne(input.getId());
        client.setAddress(input.getAddress());
        client.setName(input.getName());
        var clientContacts = client.getContacts();
        for (var contactInput: input.getContacts())
        {
            var result = contactRepository.findById(contactInput.getId());
            if(result.isEmpty())
            {
                client.addContact(Contact.builder().name(contactInput.getName()).email(contactInput.getEmail()).phone(contactInput.getPhone()).client(client).build());
            }else{
                var contact = client.findInContacts(e -> e.getId() == contactInput.getId());
                contact.setName(contactInput.getName());
                contact.setEmail(contactInput.getEmail());
                contact.setPhone(contactInput.getPhone());
            }
        }
        return repository.save(client);
    }

    @SneakyThrows
    @Override
    @PreAuthorize("hasPermission(null, 'DELETE_CLIENT')")
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

    @PostFilter("hasPermission(filterObject, 'READ_CLIENT')")
    public List<Client> getClientsTable(String search)
    {
        return repository.findAllWithContactsByNameContaining(search);
    }
}
