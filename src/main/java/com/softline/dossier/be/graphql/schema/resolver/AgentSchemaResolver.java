package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.Agent;
import com.softline.dossier.be.domain.Client;
import com.softline.dossier.be.graphql.types.input.AgentInput;
import com.softline.dossier.be.graphql.types.input.ClientInput;
import com.softline.dossier.be.repository.AgentRepository;
import com.softline.dossier.be.repository.ClientRepository;
import com.softline.dossier.be.service.AgentService;
import com.softline.dossier.be.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AgentSchemaResolver extends SchemaResolverBase<Agent, AgentInput, AgentRepository, AgentService> {


    public Agent createAgent(AgentInput agentInput){
        return create(agentInput);
    }
    public Agent updateAgent(AgentInput agentInput){
        return update(agentInput);
    }
    public boolean deleteAgent(Long id){
        return delete(id);
    }
    public List<Agent> getAllAgent(){
        return getAll();
    }
    public Agent getAgent(Long id){
        return get(id);
    }

}
