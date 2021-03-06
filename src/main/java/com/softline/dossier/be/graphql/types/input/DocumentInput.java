package com.softline.dossier.be.graphql.types.input;

import lombok.Getter;

@Getter
public class DocumentInput {
    FileActivityInput fileActivity;
    AgentInput agent;
    String description;
    Long id;
    String path;
}
