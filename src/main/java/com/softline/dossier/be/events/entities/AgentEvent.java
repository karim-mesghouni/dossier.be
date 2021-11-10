package com.softline.dossier.be.events.entities;

import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.security.domain.Agent;
import lombok.SneakyThrows;

public class AgentEvent extends EntityEvent {
    @SneakyThrows
    public AgentEvent(Type type, Agent agent) {
        super("agent" + type);
        addData("agentId", agent.getId());
    }
}
