package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.domain.Contact;
import lombok.Getter;

@Getter
public class ContactInput extends Input<Contact> implements HasId {
    Class<Contact> mappingTarget = Contact.class;
    Long id;
    String name;
    String phone;
    String email;
    ClientInput client;
}
