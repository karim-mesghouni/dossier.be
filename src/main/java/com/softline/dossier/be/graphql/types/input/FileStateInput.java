package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.domain.FileStateType;
import lombok.Getter;

@Getter
public class FileStateInput extends Input<FileStateType> implements HasId {
    Class<FileStateType> mappingTarget = FileStateType.class;

    boolean current;
    FileStateTypeInput type;
    FileInput file;
    Long id;
}
