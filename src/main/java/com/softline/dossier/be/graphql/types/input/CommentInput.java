package com.softline.dossier.be.graphql.types.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;


@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentInput  {


    Long id;

    String content;

    FileInput file;
    AgentInput agent;
 Date   createdDate;
}
