package com.softline.dossier.be.service;

import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.Contact;
import com.softline.dossier.be.graphql.types.input.ContactInput;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    public List<Contact> getAll() {
        return Database.findAll(Contact.class);
    }

    public Contact update(ContactInput input) {
        var contact = Database.findOrThrow(input.map());
        Database.startTransaction();
        contact.setName(input.getName());
        contact.setEmail(input.getEmail());
        contact.setPhone(input.getPhone());
        Database.commit();
        return contact;
    }

    @PreAuthorize("hasPermission(null, 'DELETE_CONTACT')")
    public boolean delete(long id) {
        Database.removeNow(Contact.class, id);
        return true;
    }
}
