package com.softline.dossier.be.graphql.schema.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.domain.Contact;
import com.softline.dossier.be.graphql.types.input.ContactInput;
import com.softline.dossier.be.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ContactSchemaResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    private final ContactService service;

    public Contact updateContact(ContactInput clientInput) {
        return service.update(clientInput);
    }

    public boolean deleteContact(Long id) {
        return service.delete(id);
    }
}
