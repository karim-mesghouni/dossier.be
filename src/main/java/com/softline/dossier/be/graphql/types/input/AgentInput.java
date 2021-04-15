package com.softline.dossier.be.graphql.types.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AgentInput  {

    Long id;
    String name;
     String username;

     String password;
}
