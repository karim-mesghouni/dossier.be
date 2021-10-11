package com.softline.dossier.be.graphql.types.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileStateInput
{

    boolean current;
    FileStateTypeInput type;
    FileInput file;
    private long id;


}
