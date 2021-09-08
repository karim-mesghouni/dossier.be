package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.Client;
import com.softline.dossier.be.domain.Contact;
import com.softline.dossier.be.graphql.types.input.ClientInput;
import com.softline.dossier.be.graphql.types.input.ContactInput;
import com.softline.dossier.be.repository.ClientRepository;
import com.softline.dossier.be.repository.ContactRepository;
import com.softline.dossier.be.service.ClientService;
import com.softline.dossier.be.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")

public class ContactSchemaResolver extends SchemaResolverBase<Contact, ContactInput, ContactRepository, ContactService> {
    public Contact createContact(ContactInput clientInput) throws IOException {
        return create(clientInput);
    }
    public Contact updateContact(ContactInput clientInput){
        return update(clientInput);
    }
    public boolean deleteContact(Long id){
        return delete(id);
    }
}
