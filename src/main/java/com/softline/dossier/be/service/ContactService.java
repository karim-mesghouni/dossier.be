package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.Contact;
import com.softline.dossier.be.graphql.types.input.ContactInput;
import com.softline.dossier.be.repository.ClientRepository;
import com.softline.dossier.be.repository.ContactRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional

@Service
public class ContactService extends IServiceBase<Contact, ContactInput, ContactRepository> {
    @Autowired
    ClientRepository clientRepository;

    @Override
    public List<Contact> getAll() {
        return repository.findAll();
    }

    @Override
    public Contact create(ContactInput input) {
//        Client client = clientRepository.findById(input.getClient().getId()).orElseThrow();
//        return repository.save(Contact.builder().name(input.getName()).email(input.getEmail()).phone(input.getPhone()).client(client).build());
        return null;
    }

    @Override
    public Contact update(ContactInput input) {
        Contact contact = repository.findById(input.getId()).orElseThrow();
        contact.setName(input.getName());
        contact.setEmail(input.getEmail());
        contact.setPhone(input.getPhone());
        return repository.save(contact);
    }

    @SneakyThrows
    @Override
    @PreAuthorize("hasPermission(null, 'DELETE_CONTACT')")
    public boolean delete(long id) {
        repository.delete(repository.findById(id).orElseThrow());
        return true;
    }

    @Override
    public Contact getById(long id) {
        return null;
    }
}
