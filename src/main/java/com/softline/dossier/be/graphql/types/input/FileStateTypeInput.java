package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.Getter;

@Getter
public class FileStateTypeInput implements HasId {

    String state;
    long id;
}
