package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.domain.Concerns.HasSoftDelete;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.domain.RoleInput;
import lombok.Getter;

@Getter
public class AgentInput extends Input<Agent> implements HasId, HasSoftDelete {
    Class<Agent> mappingTarget = Agent.class;

    Long id;
    boolean deleted;

    String name;
    String username;
    String password;

    JobInput job;
    ActivityInput activity;
    RoleInput role;
}
