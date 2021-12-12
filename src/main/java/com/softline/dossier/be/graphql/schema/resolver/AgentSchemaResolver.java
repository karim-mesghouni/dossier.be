package com.softline.dossier.be.graphql.schema.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.Job;
import com.softline.dossier.be.graphql.types.input.AgentInput;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.domain.Role;
import com.softline.dossier.be.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.List;

@Component
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class AgentSchemaResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    private final AgentService service;

    public List<Agent> getAllAgent() {
        return service.getAll();
    }

    public Agent getAgent(Long id) {
        return service.getById(id);
    }

    public Agent getCurrentAgent() {
        return service.getCurrentAgent();
    }

    public List<Role> allRoles() {
        return Database.findAll(Role.class);
    }

    public List<Job> allJobs() {
        return Database.findAll(Job.class);
    }


    public Agent updateAgent(AgentInput input) {
        return service.update(input.map());
    }

    public boolean changePassword(AgentInput input, String oldPassword) {
        return service.changePassword(input.map(), oldPassword);
    }

    public Agent createAgent(AgentInput input) {
        return service.create(input.map());
    }

    public void deleteAgent(long id) {
        service.delete(id);
    }

    public List<Agent> findAgentBySearch(@Nullable String search) {
        return service.findBySearch(search);
    }
}
