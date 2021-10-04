package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.graphql.types.input.AgentInput;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.repository.AgentRepository;
import com.softline.dossier.be.service.AgentService;
import com.softline.dossier.be.service.exceptions.ClientReadableException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AgentSchemaResolver extends SchemaResolverBase<Agent, AgentInput, AgentRepository, AgentService> {


    public Agent createAgent(AgentInput agentInput) throws IOException, ClientReadableException
    {
        return create(agentInput);
    }

    public Agent updateAgent(AgentInput agentInput) throws ClientReadableException
    {
        return update(agentInput);
    }

    public boolean deleteAgent(Long id) throws ClientReadableException
    {
        return delete(id);
    }

    public List<Agent> getAllAgent() {
        return getAll();
    }

    public Agent getAgent(Long id) {
        return get(id);
    }

    public Agent getCurrentAgent() {
        return service.getCurrentAgent();
    }
}
