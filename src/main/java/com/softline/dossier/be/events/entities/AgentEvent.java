package com.softline.dossier.be.events.entities;

import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.security.domain.Agent;

public class AgentEvent extends EntityEvent<Agent> {

    public AgentEvent(Type type, Agent agent) {
        super("agent" + type, agent);
        addData("agentId", agent.getId());
    }
}
