package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.security.domain.RoleInput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AgentInput
{
    Long id;
    String name;
    String username;
    String password;

    JobInput job;
    ActivityInput activity;
    RoleInput role;
}
