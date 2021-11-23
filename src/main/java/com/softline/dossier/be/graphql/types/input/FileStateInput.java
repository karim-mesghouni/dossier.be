package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.Getter;

@Getter
public class FileStateInput implements HasId {

    boolean current;
    FileStateTypeInput type;
    FileInput file;
    long id;
}
