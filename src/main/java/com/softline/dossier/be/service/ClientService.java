package com.softline.dossier.be.service;

import com.softline.dossier.be.Tools.Functions;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.Client;
import com.softline.dossier.be.domain.Contact;
import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.ClientEvent;
import com.softline.dossier.be.graphql.types.input.ClientInput;
import graphql.GraphQLException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {
    public List<Client> getAll() {
        return Database.findAll(Client.class);
    }

    public Client getById(Long id) {
        return Database.findOrThrow(Client.class, id);
    }

    public List<Client> getClientsTable(String search) {
        if (search == null || search.isBlank()) return Database.findAll(Client.class);
        return Database.findAll(Client.class, (cq, cb, r) -> cq.where(cb.like(r.get("name"), "%" + search + "%")));
    }

    @PreAuthorize("hasPermission(null, 'CREATE_CLIENT')")
    public Client create(Client client) {
        Database.startTransaction();
        this.validateClientInput(client);
        client.setId(null);
        client.getContacts().removeIf(ct -> ct.getEmail() == null && ct.getName() == null && ct.getPhone() == null);
        client.getContacts().forEach(ct -> ct.setId(null));
        Database.persist(client);
        Database.commit();
        new ClientEvent(EntityEvent.Type.ADDED, client).fireToAll();
        return client;
    }

    private void validateClientInput(Client client) {
        Functions.throwIfEmpty(client.getName(), () -> new GraphQLException("le nom du client est requis"));
        client.getContacts()
                .stream()
                .filter(ct -> ct.getId() != null && ct.getId() != 0)
                .collect(Collectors.toList()).
                forEach(ct -> {
                    if (Functions.isEmpty(ct::getEmail) && Functions.isEmpty(ct::getName) && Functions.isEmpty(ct::getPhone))
                        throw new GraphQLException("contact ne peut pas être vide");
                });
    }

    public Client update(ClientInput input) {
        return Database.findOrThrow(Client.class, input, "UPDATE_CLIENT", client -> {
            Database.startTransaction();
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
            this.validateClientInput(client);
            Database.commit();
            new ClientEvent(EntityEvent.Type.UPDATED, client).fireToAll();
            return client;
        });
    }

    public boolean delete(Long id) {
        return Database.afterRemoving(Client.class, id, "DELETE_CLIENT", client -> {
            new ClientEvent(EntityEvent.Type.DELETED, client).fireToAll();
        });
    }

}
