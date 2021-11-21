package com.softline.dossier.be.service;

import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.Client;
import com.softline.dossier.be.domain.Contact;
import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.ClientEvent;
import com.softline.dossier.be.graphql.types.input.ClientInput;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class ClientService {
    public List<Client> getAll() {
        return Database.findAll(Client.class);
    }

    @PreAuthorize("hasPermission(null, 'CREATE_CLIENT')")
    public Client create(ClientInput input) {
        Client client = Client.builder().name(input.getName()).address(input.getAddress()).build();
        List<Contact> contacts = new ArrayList<>();
        for (var contact : input.getContacts()) {
            contacts.add(Contact.builder().name(contact.getName()).email(contact.getEmail()).phone(contact.getPhone()).client(client).build());
        }
        client.setContacts(contacts);
        Database.persist(client);
        Database.flush();
        new ClientEvent(EntityEvent.Type.ADDED, client).fireToAll();
        return client;
    }

    public Client update(ClientInput input) {
        return Database.findOrThrow(Client.class, input, "UPDATE_CLIENT", client -> {
            client.setAddress(input.getAddress());
            client.setName(input.getName());
            for (var contactInput : input.getContacts()) {
                Contact contact = Database.findOrNull(Contact.class, contactInput);
                if (contact == null) {
                    client.addContact(Contact.builder().name(contactInput.getName()).email(contactInput.getEmail()).phone(contactInput.getPhone()).client(client).build());
                } else {
                    contact.setName(contactInput.getName());
                    contact.setEmail(contactInput.getEmail());
                    contact.setPhone(contactInput.getPhone());
                }
            }
            Database.flush();
            new ClientEvent(EntityEvent.Type.UPDATED, client).fireToAll();
            return client;
        });
    }

    public boolean delete(long id) {
        return Database.afterRemoving(Client.class, id, "DELETE_CLIENT", client -> {
            Database.flush();
            new ClientEvent(EntityEvent.Type.DELETED, client).fireToAll();
        });
    }

    public Client getById(long id) {
        return Database.findOrThrow(Client.class, id);
    }

    public List<Client> getClientsTable(String search) {
        if (search == null || search.isBlank()) return Database.findAll(Client.class);
        return Database.findAll(Client.class, (cq, cb, r) -> cq.where(cb.like(r.get("name"), "%" + search + "%")));
    }
}
