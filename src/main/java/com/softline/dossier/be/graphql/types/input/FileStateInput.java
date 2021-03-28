package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.BaseEntity;
import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.domain.FileStateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileStateInput {

    private long id ;

    boolean current;

    FileStateTypeInput type;

    FileInput file;


}
