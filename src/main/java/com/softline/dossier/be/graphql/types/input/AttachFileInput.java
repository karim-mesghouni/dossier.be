package com.softline.dossier.be.graphql.types.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachFileInput {

    public String name;
    FileTaskInput fileTask;
    private long id;
    private String path;
}
