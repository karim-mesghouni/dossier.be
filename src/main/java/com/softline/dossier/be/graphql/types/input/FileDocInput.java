package com.softline.dossier.be.graphql.types.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class FileDocInput {

    FileInput file;
    FileActivityInput fileActivity;
    AgentInput agent;
    String description;
    private long id;
    private String path;
}
