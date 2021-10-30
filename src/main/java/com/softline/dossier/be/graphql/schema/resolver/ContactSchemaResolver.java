package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.Contact;
import com.softline.dossier.be.graphql.types.input.ContactInput;
import com.softline.dossier.be.repository.ContactRepository;
import com.softline.dossier.be.service.ContactService;
import com.softline.dossier.be.service.exceptions.ClientReadableException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")

public class ContactSchemaResolver extends SchemaResolverBase<Contact, ContactInput, ContactRepository, ContactService> {
    public Contact createContact(ContactInput clientInput) throws IOException, ClientReadableException {
        return create(clientInput);
    }

    public Contact updateContact(ContactInput clientInput) throws ClientReadableException {
        return update(clientInput);
    }

    public boolean deleteContact(Long id) throws ClientReadableException {
        return delete(id);
    }
}
