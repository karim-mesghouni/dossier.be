package com.softline.dossier.be.events;

import com.softline.dossier.be.events.types.EntityEvent;
import com.softline.dossier.be.security.domain.Agent;
import lombok.SneakyThrows;

public class AgentEvent extends EntityEvent {
    @SneakyThrows
    public AgentEvent(Event type, Agent agent) {
        super("agent" + type);
        addData("agentId", agent.getId());
    }
}
