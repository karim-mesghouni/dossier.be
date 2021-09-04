package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Contact;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ClientInput  {

    private long id;
    private String name;
    private String address;
    List<FileInput> files;
    List<VisAVisInput> visAVis;
    List<ContactInput> contacts;


}
