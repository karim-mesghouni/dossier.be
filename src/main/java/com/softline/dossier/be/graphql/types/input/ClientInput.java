package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Client;
import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.Getter;

import java.util.List;

@Getter
public class ClientInput extends Input<Client> implements HasId {
    Class<Client> mappingTarget = Client.class;

    List<FileInput> files;
    List<VisAVisInput> visAVis;
    List<ContactInput> contacts;
    long id;
    String name;
    String address;


}
